/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.groovy.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.MetaMethod;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.kb.CachedScriptClassInstancePovider;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.groovy.GroovyAction;
import org.openksavi.sponge.groovy.GroovyConstants;
import org.openksavi.sponge.groovy.GroovyCorrelator;
import org.openksavi.sponge.groovy.GroovyFilter;
import org.openksavi.sponge.groovy.GroovyPlugin;
import org.openksavi.sponge.groovy.GroovyRule;
import org.openksavi.sponge.groovy.GroovyTrigger;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Knowledge base interpreter supporting knowledge base to be defined in the Groovy language.
 */
public class GroovyKnowledgeBaseInterpreter extends BaseScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(GroovyKnowledgeBaseInterpreter.class);

    public static final String PROP_CLASSPATH = "groovy.classpath";

    @SuppressWarnings("rawtypes")
    //@formatter:off
    protected static final Map<Class, Class> PROCESSOR_CLASSES = Utils.immutableMapOf(
            Action.class, GroovyAction.class,
            Filter.class, GroovyFilter.class,
            Trigger.class, GroovyTrigger.class,
            Rule.class, GroovyRule.class,
            Correlator.class, GroovyCorrelator.class
            );
    //@formatter:on

    /** Groovy shell. This is the interface to Groovy used by the engine. */
    private GroovyShell shell;

    private Binding binding;

    private List<Script> scripts;

    public GroovyKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new GroovyKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), GroovyConstants.TYPE);
    }

    @Override
    protected void prepareInterpreter() {
        ImportCustomizer importCustomizer = new ImportCustomizer();

        PROCESSOR_CLASSES
                .forEach((interfaceClass, scriptClass) -> addImport(importCustomizer, scriptClass, interfaceClass.getSimpleName()));
        addImport(importCustomizer, GroovyPlugin.class, Plugin.class.getSimpleName());

        getStandardImportClasses().forEach(cls -> addImport(importCustomizer, cls));

        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(importCustomizer);

        binding = createBinding();
        shell = new GroovyShell(binding, configuration);
        scripts = Collections.synchronizedList(new ArrayList<>());

        setVariable(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, getEngineOperations());

        setClasspath(getEngineOperations() != null ? getEngineOperations().getEngine() : null);
    }

    protected Binding createBinding() {
        return new Binding(Collections.synchronizedMap(new HashMap<>()));
    }

    /**
     * Clears the interpreter state.
     */
    @Override
    public void onClear() {
        synchronized (interpteterSynchro) {
            invalidateCache();

            if (scripts != null) {
                scripts.clear();
                scripts = null;
            }

            shell = null;
            binding = null;
        }
    }

    private void setClasspath(Engine engine) {
        if (engine != null) {
            String claaspath = getEngineOperations().getEngine().getConfigurationManager().getProperty(PROP_CLASSPATH);
            if (claaspath != null) {
                Stream.of(StringUtils.split(claaspath, PROP_PATH_SEPARATOR)).forEach(path -> shell.getClassLoader().addClasspath(path));
            }
        }
    }

    @Override
    public void setVariable(String name, Object value) {
        try {
            binding.setProperty(name, value);
            scripts.forEach(script -> script.setProperty(name, value));
        } catch (Throwable e) {
            throw Utils.wrapException("setVariable", e);
        }
    }

    /**
     * Result {@code null} means that there is no variable. Result other than {@code null} means that there is a variable (that may possibly
     * be {@code null}).
     *
     * @param name the name of the variable.
     * @return a holder for a variable.
     */
    protected Mutable<Object> doGetVariable(String name) {
        List<Object> variables =
                scripts.stream().filter(script -> script.getMetaClass().hasProperty(script.getMetaClass().getTheClass(), name) != null)
                        .map(script -> script.getProperty(name)).collect(Collectors.toList());

        if (variables.isEmpty()) {
            try {
                return new MutableObject<>(binding.getProperty(name));
            } catch (MissingPropertyException e) {
                return null; // This means that no variable has been found!
            }
        }

        return new MutableObject<>(variables.get(0));
    }

    @Override
    public Object getVariable(String name) {
        try {
            Mutable<Object> variable = doGetVariable(name);
            if (variable == null) {
                throw new SpongeException("Variable '" + name + "' not found in any of the scripts");
            }

            return variable.getValue();
        } catch (Throwable e) {
            throw Utils.wrapException("getVariable", e);
        }
    }

    @Override
    public boolean existsVariable(String name) {
        try {
            return doGetVariable(name) != null;
        } catch (Throwable e) {
            throw Utils.wrapException("existsVariable", e);
        }
    }

    /**
     * Adds import from the package.
     *
     * @param importCustomizer Groovy import customizer.
     * @param clazz class to be imported.
     */
    protected void addImport(ImportCustomizer importCustomizer, Class<?> clazz) {
        addImport(importCustomizer, clazz, clazz.getSimpleName());
    }

    protected void addImport(ImportCustomizer importCustomizer, Class<?> clazz, String alias) {
        importCustomizer.addImport(alias, clazz.getName());
    }

    @Override
    public void invokeOptionalFunction(String name) {
        try {
            doInvokeFunction(name, true, null);
        } catch (Throwable e) {
            throw Utils.wrapException(name, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invokeFunction(String name, Class<T> cls, Object... args) {
        try {
            return (T) doInvokeFunction(name, false, args);
        } catch (Throwable e) {
            throw Utils.wrapException(name, e);
        }
    }

    protected Object doInvokeFunction(String name, boolean optional, Object[] args) {
        Object result = null;
        boolean invoked = false;

        for (Script script : scripts) {
            MetaMethod method = script.getMetaClass().getMetaMethod(name, args != null ? args : new Object[0]);
            if (method != null) {
                if (invoked) {
                    // Invoke only the last function of the same name. This is required for compatibility with other supported
                    // scripting languages.
                    break;
                }
                result = script.invokeMethod(name, args);
                invoked = true;
            }
        }

        if (!invoked) {
            if (optional) {
                return null;
            } else {
                throw new SpongeException("Missing function '" + name + "'");
            }
        }

        return result;
    }

    @Override
    public boolean isKnowledgeBaseException(Throwable exception) {
        return Utils.containsException(exception, GroovyRuntimeException.class);
    }

    @Override
    public Throwable getJavaException(Throwable knowledgeBaseException) {
        return Utils.getException(knowledgeBaseException, GroovyRuntimeException.class);
    }

    /**
     * Returns {@code null} if not script-based processor.
     */
    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        if (processorClass instanceof GroovyClassWrapper) {
            GroovyClassWrapper classWrapper = (GroovyClassWrapper) processorClass;

            return classWrapper.getWrappedClass().getName();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(String code) {
        try {
            return (T) shell.evaluate(code);
        } catch (Throwable e) {
            throw Utils.wrapException("eval", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(Reader reader, String fileName) {
        try {
            return (T) shell.evaluate(reader, fileName);
        } catch (Throwable e) {
            throw Utils.wrapException(fileName, e);
        }
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        try {
            return ((GroovyObject) target).invokeMethod(name, args);
        } catch (Throwable e) {
            throw Utils.wrapException(target + "." + name, e);
        }
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInterpreterInstance(Engine engine, KnowledgeBase knowledgeBase) {
        return new GroovyKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    @Override
    protected void doReload(List<KnowledgeBaseScript> scripts) {
        if (scripts != null) {
            scripts.clear();
        }

        super.doReload(scripts);
    }

    @Override
    protected void doLoad(Reader reader, String fileName) {
        Script script = shell.parse(reader, fileName);
        script.setBinding(binding);
        script.run();

        // Add the last script as the first.
        scripts.add(0, script);
    }

    private List<String> createClasspath(Engine engine) {
        List<String> result = new ArrayList<>();
        if (engine != null) {
            String classpath = getEngineOperations().getEngine().getConfigurationManager().getProperty(PROP_CLASSPATH);
            if (classpath != null) {
                result.addAll(Arrays.asList(StringUtils.split(classpath, PROP_PATH_SEPARATOR)));
            }
        }

        return result;
    }

    public void reloadClass(Class<?> clazz) {
        try {
            shell.getClassLoader().loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw Utils.wrapException("reloadClass", e);
        }
    }

    public Script reloadScript(String scriptName) {
        try {
            invalidateCache();

            GroovyScriptEngine groovy = new GroovyScriptEngine(createClasspath(getEngineOperations().getEngine()).toArray(new String[0]),
                    shell.getClassLoader());
            Script script = groovy.createScript(scriptName, binding);
            script.run();
            return script;
        } catch (IOException | ResourceException | ScriptException e) {
            throw Utils.wrapException("reloadScript", e);
        }
    }

    @Override
    public void scanToAutoEnable() {
        List<Object> autoEnabled = new ArrayList<>();
        Stream.of(shell.getClassLoader().getLoadedClasses()).forEach(cls -> {
            if (PROCESSOR_CLASSES.values().stream().filter(processorClass -> ClassUtils.isAssignable(cls, processorClass)).findFirst()
                    .isPresent()) {
                String name = cls.getName();
                if (!isProcessorAbstract(name)) {
                    autoEnabled.add(name);
                    ((GroovyKnowledgeBaseEngineOperations) getEngineOperations()).enable(cls);
                }
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected <T> CachedScriptClassInstancePovider createCachedScriptClassInstancePovider() {
        return new CachedScriptClassInstancePovider<Script, T>(getEngineOperations().getEngine(), (expression) -> shell.parse(expression),
                "new %s()", (script, javaClass) -> (T) script.run());
    }
}
