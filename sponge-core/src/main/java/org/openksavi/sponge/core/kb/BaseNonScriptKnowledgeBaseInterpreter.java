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

package org.openksavi.sponge.core.kb;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Non script based knowledge base interpreter.
 */
public abstract class BaseNonScriptKnowledgeBaseInterpreter extends BaseKnowledgeBaseInterpreter {

    protected Map<String, Object> variables = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Creates a new knowledge base interpreter.
     *
     * @param engineOperations an engine operations.
     * @param type a knowledge base type.
     */
    protected BaseNonScriptKnowledgeBaseInterpreter(KnowledgeBaseEngineOperations engineOperations, KnowledgeBaseType type) {
        super(engineOperations, type);
    }

    @Override
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    @Override
    public Object getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new SpongeException("Variable " + name + " not found");
        }

        return variables.get(name);
    }

    @Override
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    @Override
    public boolean isKnowledgeBaseException(Throwable exception) {
        return false;
    }

    @Override
    public Throwable getJavaException(Throwable knowledgeBaseException) {
        return knowledgeBaseException;
    }

    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        return null;
    }
}
