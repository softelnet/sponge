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
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Script Engine (JSR-223) based knowledge base interpreter.
 */
public abstract class EngineScriptKnowledgeBaseInterpreter extends BaseScriptKnowledgeBaseInterpreter {

    /** Script engine instance. */
    protected ScriptEngine scriptEngine;

    /**
     * Creates a new Knowledge Interpreter.
     *
     * @param engineOperations an engine operations.
     * @param type a knowledge base type.
     */
    protected EngineScriptKnowledgeBaseInterpreter(KnowledgeBaseEngineOperations engineOperations, KnowledgeBaseType type) {
        super(engineOperations, type);
    }

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
            scriptEngine = null;
        }
    }

    protected Invocable getInvocable() {
        return (Invocable) scriptEngine;
    }

    @Override
    public void invokeOptionalFunction(String name) {
        try {
            getInvocable().invokeFunction(name);
        } catch (NoSuchMethodException e) {
            // Ignore non existing, optional function.
        } catch (ScriptException e) {
            throw Utils.wrapException(name, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T invokeFunction(String name, Class<T> cls, Object... args) {
        try {
            return (T) getInvocable().invokeFunction(name, args);
        } catch (NoSuchMethodException | ScriptException e) {
            throw Utils.wrapException(name, e);
        }
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        try {
            return getInvocable().invokeMethod(target, name, args);
        } catch (NoSuchMethodException | ScriptException e) {
            throw Utils.wrapException(target + "." + name, e);
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
            throw Utils.wrapException("setVariable", e);
        }
    }

    @Override
    public boolean existsVariable(String name) {
        try {
            return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).containsKey(name);
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
            return scriptEngine.get(name);
        } catch (Throwable e) {
            throw Utils.wrapException("getVariable", e);
        }
    }

    @Override
    public <T> T eval(String expression) {
        try {
            return eval(scriptEngine, expression);
        } catch (Throwable e) {
            throw Utils.wrapException("eval", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T eval(ScriptEngine scriptEngine, String expression) {
        try {
            return (T) scriptEngine.eval(expression);
        } catch (Throwable e) {
            throw Utils.wrapException("eval", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T eval(Reader reader, String fileName) {
        try {
            return (T) scriptEngine.eval(reader);
        } catch (Throwable e) {
            throw Utils.wrapException(fileName, e);
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
        return Utils.containsException(exception, ScriptException.class);
    }

    @Override
    public Throwable getJavaException(Throwable knowledgeBaseException) {
        return Utils.getException(knowledgeBaseException, ScriptException.class);
    }
}
