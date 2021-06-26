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

package org.openksavi.sponge.core.kb;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.core.util.exception.SpongeExceptionUtils;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Script Engine (JSR-223) based knowledge base interpreter.
 */
public abstract class EngineScriptKnowledgeBaseInterpreter extends BaseScriptKnowledgeBaseInterpreter {

    /** Script engine instance. */
    private ScriptEngine scriptEngine;

    /**
     * Creates a new Knowledge Interpreter.
     *
     * @param engineOperations an engine operations.
     * @param type a knowledge base type.
     */
    protected EngineScriptKnowledgeBaseInterpreter(KnowledgeBaseEngineOperations engineOperations, KnowledgeBaseType type) {
        super(engineOperations, type);
    }

    protected abstract String getScriptClassInstancePoviderFormat();

    /**
     * Creates scripting engine.
     *
     * @return scripting engine.
     */
    protected abstract ScriptEngine createScriptEngine();

    /**
     * Prepares the interpreter.
     */
    @Override
    protected void prepareInterpreter() {
        scriptEngine = createScriptEngine();
    }

    /**
     * Clears the interpreter state.
     */
    @Override
    public void onClear() {
        synchronized (interpteterSynchro) {
            invalidateCache();

            scriptEngine = null;
        }
    }

    protected Invocable getInvocable() {
        return (Invocable) scriptEngine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invokeOptionalFunction(String name, T defaultValue) {
        try {
            return (T) getInvocable().invokeFunction(name);
        } catch (NoSuchMethodException e) {
            // Ignore non existing, optional function.
            return defaultValue;
        } catch (ScriptException e) {
            throw SpongeUtils.wrapException(name, this, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invokeFunction(String name, Class<T> cls, Object... args) {
        try {
            return (T) getInvocable().invokeFunction(name, args);
        } catch (NoSuchMethodException | ScriptException e) {
            throw SpongeUtils.wrapException(name, this, e);
        }
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        try {
            return getInvocable().invokeMethod(target, name, (Object[]) args);
        } catch (NoSuchMethodException | ScriptException e) {
            throw SpongeUtils.wrapInvokeException(target, name, this, e);
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
        try {
            scriptEngine.put(name, value);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(this, e);
        }
    }

    @Override
    public boolean hasVariable(String name) {
        try {
            return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).containsKey(name);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(this, e);
        }
    }

    @Override
    public Object getVariable(String name) {
        if (!hasVariable(name)) {
            throw new SpongeException("Variable '" + name + "' not found");
        }

        try {
            return scriptEngine.get(name);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException("getVariable", this, e);
        }
    }

    @Override
    public <T> T eval(String expression) {
        try {
            return eval(scriptEngine, expression);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException("eval", this, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T eval(ScriptEngine scriptEngine, String expression) {
        try {
            return (T) scriptEngine.eval(expression);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException("eval", this, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(Reader reader, String filename) {
        Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        Object prevFilename = bindings.get(ScriptEngine.FILENAME);

        try {
            bindings.put(ScriptEngine.FILENAME, filename);

            return (T) scriptEngine.eval(reader);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(filename, this, e);
        } finally {
            bindings.put(ScriptEngine.FILENAME, prevFilename);
        }
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public String resolveVariableName(Object value) {
        Bindings bindings = getScriptEngine().getBindings(ScriptContext.ENGINE_SCOPE);
        Iterator<Entry<String, Object>> iterator = bindings.entrySet().stream().filter(entry -> entry.getValue() == value).iterator();
        if (!iterator.hasNext()) {
            throw new SpongeException("Variable for processor " + value + " not found.");
        }
        Map.Entry<String, Object> variableEntry = iterator.next();
        if (iterator.hasNext()) {
            throw new SpongeException("Multiple variables for processor " + value + " have been found.");
        }

        return variableEntry.getKey();
    }

    @Override
    public boolean isKnowledgeBaseException(Throwable exception) {
        return SpongeExceptionUtils.containsException(exception, ScriptException.class);
    }

    @Override
    public Throwable getJavaException(Throwable knowledgeBaseException) {
        return SpongeExceptionUtils.getException(knowledgeBaseException, ScriptException.class);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected <T> ScriptClassInstanceProvider<T> createScriptClassInstancePovider() {
        return new CachedScriptClassInstancePovider<CompiledScript, T>(getEngineOperations().getEngine(), (expression) -> {
            try {
                return ((Compilable) scriptEngine).compile(expression);
            } catch (ScriptException e) {
                throw SpongeUtils.wrapException(this, e);
            }
        }, getScriptClassInstancePoviderFormat(), (script, javaClass) -> {
            try {
                return (T) script.eval();
            } catch (ScriptException e) {
                throw SpongeUtils.wrapException(this, e);
            }
        });
    }

    protected void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }
}
