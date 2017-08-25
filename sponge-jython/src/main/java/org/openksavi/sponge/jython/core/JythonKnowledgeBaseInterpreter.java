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

package org.openksavi.sponge.jython.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyType;
import org.python.jsr223.PyScriptEngineScope;
import org.python.jsr223.PyScriptEngineScope.ScopeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.EngineScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.jython.JythonAction;
import org.openksavi.sponge.jython.JythonCorrelator;
import org.openksavi.sponge.jython.JythonFilter;
import org.openksavi.sponge.jython.JythonPlugin;
import org.openksavi.sponge.jython.JythonRule;
import org.openksavi.sponge.jython.JythonTrigger;
import org.openksavi.sponge.jython.PythonConstants;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Knowledge base interpreter supporting knowledge base to be defined in the Jython (Python) language.
 */
public class JythonKnowledgeBaseInterpreter extends EngineScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(JythonKnowledgeBaseInterpreter.class);

    public static final String PROP_PYTHON_PATH = "python.path";

    public static final String SCRIPT_ENGINE_NAME = "python";

    @SuppressWarnings("rawtypes")
    //@formatter:off
    protected static final Map<Class, Class> PROCESSOR_CLASSES = ImmutableMap.of(
            Action.class, JythonAction.class,
            Filter.class, JythonFilter.class,
            Trigger.class, JythonTrigger.class,
            Rule.class, JythonRule.class,
            Correlator.class, JythonCorrelator.class
            );
    //@formatter:on

    public JythonKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new JythonKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), PythonConstants.TYPE);
    }

    @Override
    protected ScriptEngine createScriptEngine() {
        setPythonPath(getEngineOperations().getEngine());

        String scripEngineName = SCRIPT_ENGINE_NAME;
        ScriptEngine result = new ScriptEngineManager().getEngineByName(scripEngineName);

        Validate.isInstanceOf(Compilable.class, result, "ScriptingEngine %s doesn't implement Compilable", scripEngineName);
        Validate.isInstanceOf(Invocable.class, result, "ScriptingEngine %s doesn't implement Invocable", scripEngineName);

        PROCESSOR_CLASSES.forEach((interfaceClass, scriptClass) -> addImport(result, scriptClass, interfaceClass.getSimpleName()));
        addImport(result, JythonPlugin.class, Plugin.class.getSimpleName());

        getStandardImportClasses().forEach(cls -> addImport(result, cls));

        result.put(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, getEngineOperations());

        return result;
    }

    private void setPythonPath(Engine engine) {
        if (engine != null) {
            String pythonPath = engine.getConfigurationManager().resolveProperty(PROP_PYTHON_PATH);
            if (pythonPath != null) {
                PySystemState engineSys = new PySystemState();
                List<String> paths = Arrays.asList(StringUtils.split(pythonPath, PROP_PATH_SEPARATOR));
                Collections.reverse(paths);
                for (String pathElement : paths) {
                    engineSys.path.add(0, Py.newString(pathElement));
                }

                Py.setSystemState(engineSys);
            }
        }
    }

    /**
     * Adds import from the package.
     *
     * @param scriptEngine scripting engine.
     * @param clazz class to be imported.
     */
    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz) {
        eval(scriptEngine, "from " + clazz.getPackage().getName() + " import " + clazz.getSimpleName());
    }

    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz, String alias) {
        eval(scriptEngine, "from " + clazz.getPackage().getName() + " import " + clazz.getSimpleName() + " as " + alias);
    }

    /**
     * Returns {@code null} if not script-based processor.
     */
    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        if (processorClass instanceof PyType) {
            PyType pyType = (PyType) processorClass;

            return pyType.getName();
        }

        return null;
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInterpreterInstance(Engine engine, KnowledgeBase knowledgeBase) {
        return new JythonKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    @Override
    public void scanToAutoEnable() {
        PyScriptEngineScope scope = eval("globals()");

        List<PyType> processorPyTypes =
                PROCESSOR_CLASSES.values().stream().map(cls -> (PyType) Py.java2py(cls)).collect(Collectors.toList());

        List<Object> autoEnabled = new ArrayList<>();
        Streams.stream(((ScopeIterator) scope.__iter__()).iterator()).forEach(element -> {
            String name = element.toString();
            PyObject pyObject = scope.__finditem__(name);

            if (pyObject != null && pyObject instanceof PyType) {
                PyType pyType = (PyType) pyObject;
                if (processorPyTypes.stream().filter(processorClass -> !pyType.equals(processorClass) && pyType.isSubType(processorClass))
                        .findFirst().isPresent()) {
                    autoEnabled.add(name);
                    ((JythonKnowledgeBaseEngineOperations) getEngineOperations()).enable(pyType);
                }
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }

    @Override
    protected String getScriptClassInstancePoviderFormat() {
        return "%s()";
    }
}
