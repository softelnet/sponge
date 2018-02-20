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

package org.openksavi.sponge.kb;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Script based knowledge base interpreter.
 */
public interface ScriptKnowledgeBaseInterpreter extends KnowledgeBaseInterpreter, KnowledgeBaseOperations {

    /**
     * Loads scripts.
     *
     * @param scripts knowledge base scripts.
     */
    void load(List<KnowledgeBaseScript> scripts);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     */
    void load(String fileName);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     * @param charset charset.
     */
    void load(String fileName, String charset);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     * @param charset charset.
     */
    void load(String fileName, Charset charset);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     * @param charset charset.
     * @param required {@code true} if the file is required.
     */
    void load(String fileName, Charset charset, boolean required);

    /**
     * Loads the knowledge base from the script.
     *
     * @param script the knowledge base script.
     */
    void load(KnowledgeBaseScript script);

    /**
     * Reloads scripts.
     *
     * @param scripts knowledge base scripts.
     */
    void reload(List<KnowledgeBaseScript> scripts);

    /**
     * Invokes a knowledge base function.
     *
     * @param name a function name.
     * @param defaultValue a default value that will be returned when there is no such function.
     * @param <T> return value type.
     * @return a function return value.
     */
    <T> T invokeOptionalFunction(String name, T defaultValue);

    /**
     * Invokes a knowledge base function.
     *
     * @param name function name.
     * @param cls result type.
     * @param args function arguments.
     * @param <T> return value type.
     * @return function return value.
     */
    <T> T invokeFunction(String name, Class<T> cls, Object... args);

    /**
     * Evaluates an expression.
     *
     * @param expression expression.
     * @param <T> result type.
     * @return result.
     */
    <T> T eval(String expression);

    /**
     * Evaluates an expression.
     *
     * @param expression expression.
     * @param cls result class.
     * @param <T> result type.
     * @return result.
     */
    <T> T eval(String expression, Class<T> cls);

    /**
     * Scans this knowledge base interpreter for processor definitions (classes) in order to auto enable.
     */
    void scanToAutoEnable();
}
