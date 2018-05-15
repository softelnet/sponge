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

package org.openksavi.sponge.kotlin.script.core;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import kotlin.reflect.KClass;

import org.apache.commons.lang3.Validate;
import org.jetbrains.kotlin.cli.common.repl.CompiledClassData;
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase.CompiledKotlinScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Experimental;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.kb.EngineScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.kotlin.KPlugin;
import org.openksavi.sponge.kotlin.core.KotlinConstants;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kotlin.core.KotlinUtils;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Script-based knowledge base interpreter supporting knowledge base to be defined in the Kotlin script language. This class is currently
 * experimental and should not be used.
 */
@Experimental
public class ScriptKotlinKnowledgeBaseInterpreter extends EngineScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(ScriptKotlinKnowledgeBaseInterpreter.class);

    public static final String SCRIPT_ENGINE_NAME = "kotlin";

    private List<CompiledKotlinScript> scripts;

    public ScriptKotlinKnowledgeBaseInterpreter(SpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(new KotlinKnowledgeBaseEngineOperations((BaseSpongeEngine) engine, knowledgeBase), KotlinConstants.TYPE_SCRIPT);
    }

    @Override
    protected void prepareInterpreter() {
        scriptEngine = createScriptEngine();

        scripts = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    protected ScriptEngine createScriptEngine() {
        String scripEngineName = SCRIPT_ENGINE_NAME;
        ScriptEngine result = new ScriptEngineManager().getEngineByName(scripEngineName);

        Validate.isInstanceOf(Compilable.class, result, "ScriptingEngine %s doesn't implement Compilable", scripEngineName);
        Validate.isInstanceOf(Invocable.class, result, "ScriptingEngine %s doesn't implement Invocable", scripEngineName);

        KotlinConstants.PROCESSOR_CLASSES
                .forEach((interfaceClass, scriptClass) -> addImport(result, scriptClass, interfaceClass.getSimpleName()));
        addImport(result, KPlugin.class, Plugin.class.getSimpleName());

        // TODO The line below performs very slow in Kotlin
        eval(result, getStandardImportClasses().stream().map(cls -> "import " + cls.getName()).collect(Collectors.joining("\n")));

        setVariable(result, KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, getEngineOperations());

        return result;
    }

    @Override
    public void onClear() {
        synchronized (interpteterSynchro) {
            super.onClear();

            if (scripts != null) {
                scripts.clear();
                scripts = null;
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
        eval(scriptEngine, "import " + clazz.getName());
    }

    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz, String alias) {
        eval(scriptEngine, "import " + clazz.getName() + " as " + alias);
    }

    protected void setVariable(ScriptEngine scriptEngine, String name, Object value) {
        scriptEngine.put(name, value);
        try {
            if (value != null) {
                scriptEngine.eval(
                        "val " + name + " = bindings[\"" + name + "\"] as " + KotlinUtils.getClassNameForScriptBinding(value.getClass()));
            } else {
                scriptEngine.eval("val " + name + " = bindings[\"" + name + "\"]");
            }
        } catch (ScriptException e) {
            throw SpongeUtils.wrapException(this, e);
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
            return KotlinUtils.createProcessorName((KClass) processorClass);
        }

        return null;
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInterpreterInstance(SpongeEngine engine, KnowledgeBase knowledgeBase) {
        return new ScriptKotlinKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    @Override
    public void scanToAutoEnable() {
        scripts.forEach(script -> {
            for (CompiledClassData data : script.getCompiledData().getClasses()) {
                String component = data.component1().replaceAll(".class", "::class");
                // If a component is a main script class, then scan it for processors.
                if (KotlinUtils.isScriptMainClass(component)) {
                    KotlinUtils.scanNestedToAutoEnable((KClass<?>) eval(component),
                            (KotlinKnowledgeBaseEngineOperations) getEngineOperations(), logger);
                }
            }
        });
    }

    @Override
    protected String getScriptClassInstancePoviderFormat() {
        return "%s()";
    }

    @Override
    protected void doReload(List<KnowledgeBaseScript> scripts) {
        if (scripts != null) {
            scripts.clear();
        }

        super.doReload(scripts);
    }

    @Override
    protected void doLoad(Reader reader, String name) {
        try {
            CompiledKotlinScript script = (CompiledKotlinScript) ((Compilable) scriptEngine).compile(reader);
            script.eval();

            // Add the last script as the first.
            scripts.add(0, script);
        } catch (ScriptException e) {
            throw SpongeUtils.wrapException(this, e);
        }
    }

    // /**
    // * Turns off a script class instance compiled cache.
    // */
    // @Override
    // protected <T> ScriptClassInstanceProvider<T> createScriptClassInstancePovider() {
    // // return new SimpleScriptClassInstancePovider<T>(getEngineOperations().getEngine(),
    // // className -> eval(String.format(getScriptClassInstancePoviderFormat(), className)));
    // }
}
