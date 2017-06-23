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

package org.openksavi.sponge.java;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.kb.GenericKnowledgeBaseType;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Java based knowledge base interpreter.
 */
public class JavaKnowledgeBaseInterpreter extends BaseKnowledgeBaseInterpreter {

    public static final KnowledgeBaseType TYPE = new GenericKnowledgeBaseType("java", "class");

    protected Map<String, Object> variables = Collections.synchronizedMap(new LinkedHashMap<>());

    public JavaKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new JavaKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), TYPE);
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        try {
            return MethodUtils.invokeMethod(target, name, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SpongeException(e);
        }
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
    public boolean existsVariable(String name) {
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doCreateInstance(String className, Class<T> javaClass) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new SpongeException(e);
        }
    }
}
