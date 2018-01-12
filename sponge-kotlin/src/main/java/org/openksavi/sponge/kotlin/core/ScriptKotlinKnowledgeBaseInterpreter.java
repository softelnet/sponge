/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.kotlin.core;

import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Experimental;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.EngineScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.kotlin.KotlinConstants;
import org.openksavi.sponge.kotlin.KotlinPlugin;
import org.openksavi.sponge.plugin.Plugin;

import kotlin.reflect.KClass;

/**
 * Script- based knowledge base interpreter supporting knowledge base to be defined in the Kotlin script language. This class is currently
 * experimental and should not be used.
 */
@Experimental
public class ScriptKotlinKnowledgeBaseInterpreter extends EngineScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(ScriptKotlinKnowledgeBaseInterpreter.class);

    public static final String SCRIPT_ENGINE_NAME = "kotlin";

    protected Bindings bindings;

    public ScriptKotlinKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new KotlinKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), KotlinConstants.TYPE_SCRIPT);
    }

    @Override
    protected ScriptEngine createScriptEngine() {
        String scripEngineName = SCRIPT_ENGINE_NAME;
        ScriptEngine result = new ScriptEngineManager().getEngineByName(scripEngineName);

        Validate.isInstanceOf(Compilable.class, result, "ScriptingEngine %s doesn't implement Compilable", scripEngineName);
        Validate.isInstanceOf(Invocable.class, result, "ScriptingEngine %s doesn't implement Invocable", scripEngineName);

        KotlinConstants.PROCESSOR_CLASSES
                .forEach((interfaceClass, scriptClass) -> addImport(result, scriptClass, interfaceClass.getSimpleName()));
        addImport(result, KotlinPlugin.class, Plugin.class.getSimpleName());

        // TODO The line below performs very slow in Kotlin
        eval(result, getStandardImportClasses().stream().map(cls -> "import " + cls.getName()).collect(Collectors.joining("\n")));

        setVariable(result, KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, getEngineOperations());

        return result;
    }

    /**
     * Adds import from the package.
     *
     * @param scriptEngine scripting engine.
     * @param clazz class to be imported.
     */
    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz) {
        eval(scriptEngine, "import " + clazz.getName());
    }

    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz, String alias) {
        eval(scriptEngine, "import " + clazz.getName() + " as " + alias);
    }

    protected void setVariable(ScriptEngine scriptEngine, String name, Object value) {
        scriptEngine.put(name, value);
        try {
            if (value != null) {
                scriptEngine.eval("val " + name + " = bindings[\"" + name + "\"] as " + value.getClass().getName());
            } else {
                scriptEngine.eval("val " + name + " = bindings[\"" + name + "\"]");
            }
        } catch (ScriptException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Sets the variable value.
     *
     * @param name variable name.
     * @param value variable value.
     */
    @Override
    public void setVariable(String name, Object value) {
        setVariable(scriptEngine, name, value);
    }

    /**
     * Returns {@code null} if not script-based processor.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        if (processorClass instanceof KClass) {
            KClass ktType = (KClass) processorClass;

            return ktType.getQualifiedName();
        }

        return null;
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInterpreterInstance(Engine engine, KnowledgeBase knowledgeBase) {
        return new ScriptKotlinKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    @Override
    public void scanToAutoEnable() {
        // TODO To implement
        // throw new UnsupportedOperationException();
    }

    @Override
    protected String getScriptClassInstancePoviderFormat() {
        return "%s()";
    }
}
