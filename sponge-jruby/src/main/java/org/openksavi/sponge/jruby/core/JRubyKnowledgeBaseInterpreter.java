/*
 * Copyright 2016-2017 Softelnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.jruby.core;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jruby.RubyClass;
import org.jruby.RubySymbol;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.aggregator.Aggregator;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.jruby.JRubyAction;
import org.openksavi.sponge.jruby.JRubyAggregator;
import org.openksavi.sponge.jruby.JRubyFilter;
import org.openksavi.sponge.jruby.JRubyPlugin;
import org.openksavi.sponge.jruby.JRubyRule;
import org.openksavi.sponge.jruby.JRubyTrigger;
import org.openksavi.sponge.jruby.RubyConstants;
import org.openksavi.sponge.jruby.RubyUtils;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Knowledge base interpreter supporting knowledge base to be defined in the JRuby (Ruby) language.
 */
public class JRubyKnowledgeBaseInterpreter extends BaseScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(JRubyKnowledgeBaseInterpreter.class);

    public static final String PROP_RUBY_PATH = "ruby.path";

    @SuppressWarnings("rawtypes")
    //@formatter:off
    protected static final Map<Class, Class> PROCESSOR_CLASSES = ImmutableMap.of(
            Action.class, JRubyAction.class,
            Filter.class, JRubyFilter.class,
            Trigger.class, JRubyTrigger.class,
            Rule.class, JRubyRule.class,
            Aggregator.class, JRubyAggregator.class
            );
    //@formatter:on

    /** JRuby scripting container. This is the interface to JRuby used by the engine. */
    private ScriptingContainer container;

    public JRubyKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new JRubyKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), RubyConstants.TYPE);
    }

    @Override
    protected void prepareInterpreter() {
        container = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT);
        setLoadPaths(getEngineOperations() != null ? getEngineOperations().getEngine() : null);

        addSpecific();

        PROCESSOR_CLASSES.forEach((interfaceClass, scriptClass) -> addImport(scriptClass, interfaceClass.getSimpleName()));
        addImport(JRubyPlugin.class, Plugin.class.getSimpleName());

        getStandardImportClasses().forEach(cls -> addImport(cls));

        addImport(RubyUtils.class);

        container.put(createVariableName(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS), getEngineOperations());

        container.setErrorWriter(new JRubyLogErrorWriter());
    }

    /**
     * Clears the interpreter state.
     */
    @Override
    public void onClear() {
        synchronized (interpteterSynchro) {
            if (container != null) {
                container.terminate();
            }

            container = null;
        }
    }

    protected void addSpecific() {
        eval("include Java");
    }

    private void setLoadPaths(Engine engine) {
        if (engine != null) {
            String rubyPath = engine.getConfigurationManager().resolveProperty(PROP_RUBY_PATH);
            if (rubyPath != null) {
                List<String> paths = Arrays.asList(StringUtils.split(rubyPath, PROP_PATH_SEPARATOR));
                container.setLoadPaths(paths);
            }
        }
    }

    protected String createVariableName(String name) {
        return "$" + name;
    }

    @Override
    public void setVariable(String name, Object value) {
        try {
            container.put(createVariableName(name), value);
        } catch (Throwable e) {
            throw Utils.wrapException("setVariable", e);
        }
    }

    @Override
    public boolean existsVariable(String name) {
        try {
            return container.getProvider().getRuntime().getGlobalVariables().isDefined(createVariableName(name));
        } catch (Throwable e) {
            throw Utils.wrapException("existsVariable", e);
        }
    }

    @Override
    public Object getVariable(String name) {
        if (!existsVariable(name)) {
            throw new SpongeException("Variable '" + name + "' not found");
        }

        try {
            return container.get(createVariableName(name));
        } catch (Throwable e) {
            throw Utils.wrapException("getVariable", e);
        }
    }

    /**
     * Adds import from the package.
     *
     * @param clazz
     *            class to be imported.
     */
    protected void addImport(Class<?> clazz) {
        eval("java_import " + clazz.getName());
    }

    protected void addImport(Class<?> clazz, String alias) {
        eval("java_import " + clazz.getName());
        eval(alias + " = " + clazz.getName());
    }

    @Override
    protected <T> T doCreateInstance(String className, Class<T> javaClass) {
        return eval(className + ".new");
    }

    @Override
    public void invokeOptionalFunction(String name) {
        // Return if doesn't exist.
        if (eval("defined?(" + name + ")") == null) {
            return;
        }

        try {
            container.callMethod(null, name, Object.class);
        } catch (Throwable e) {
            throw Utils.wrapException(name, e);
        }
    }

    @Override
    public <T> T invokeFunction(String name, Class<T> cls, Object... args) {
        try {
            return container.callMethod(null, name, args, cls);
        } catch (Throwable e) {
            throw Utils.wrapException(name, e);
        }
    }

    @Override
    public boolean isKnowledgeBaseException(Throwable exception) {
        return Utils.containsException(exception, RaiseException.class);
    }

    @Override
    public Throwable getJavaException(Throwable knowledgeBaseException) {
        return Utils.getException(knowledgeBaseException, RaiseException.class);
    }

    /**
     * Returns {@code null} if not script-based processor.
     */
    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        if (processorClass instanceof RubyClass) {
            RubyClass rubyClass = (RubyClass) processorClass;

            return rubyClass.getName();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(String expression) {
        try {
            return (T) container.runScriptlet(expression);
        } catch (Throwable e) {
            throw Utils.wrapException("eval", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(Reader reader, String fileName) {
        try {
            return (T) container.runScriptlet(reader, fileName);
        } catch (Throwable e) {
            throw Utils.wrapException(fileName, e);
        }
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        try {
            IRubyObject rubyObject = JavaEmbedUtils.javaToRuby(container.getProvider().getRuntime(), target);
            return container.callMethod(rubyObject, name, args);
        } catch (Throwable e) {
            throw Utils.wrapException(target + "." + name, e);
        }
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInstance(Engine engine, KnowledgeBase knowledgeBase) {
        return new JRubyKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    public ScriptingContainer getScriptContainer() {
        return container;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void scanToAutoEnable() {
        Object evalResult = eval("Module.constants");
        if (evalResult == null || !(evalResult instanceof Collection)) {
            return;
        }

        List processorRubyTypes = PROCESSOR_CLASSES.values().stream().map(processorClass -> eval(processorClass.getSimpleName()))
                .collect(Collectors.toList());

        List<Object> autoEnabled = new ArrayList<>();
        ((Collection) evalResult).forEach(element -> {
            Object symbol = eval(((RubySymbol) element).asJavaString());
            if (symbol != null && symbol instanceof RubyClass) {
                RubyClass rubyClass = (RubyClass) symbol;
                if (!processorRubyTypes.contains(rubyClass) &&
                        CollectionUtils.containsAny(rubyClass.getAncestorList(), processorRubyTypes)) {
                    autoEnabled.add(rubyClass);
                    ((JRubyKnowledgeBaseEngineOperations) getEngineOperations()).enable(rubyClass);
                }
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }
}