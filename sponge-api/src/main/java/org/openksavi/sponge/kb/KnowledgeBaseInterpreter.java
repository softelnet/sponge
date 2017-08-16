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

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Knowledge base interpreter.
 */
public interface KnowledgeBaseInterpreter {

    /**
     * Returns knowledge base interpreter type.
     *
     * @return knowledge base interpreter type.
     */
    KnowledgeBaseType getType();

    /**
     * Invokes a method.
     *
     * @param target the processor containing the method to be invoked.
     * @param name method name.
     * @param args method arguments.
     * @return method result.
     */
    Object invokeMethod(Object target, String name, Object... args);

    /**
     * Sets the variable value.
     *
     * @param name variable name.
     * @param value variable value.
     */
    void setVariable(String name, Object value);

    /**
     * Returns the value of the variable. Throws exception if not found.
     *
     * @param name variable name.
     * @return variable value.
     */
    Object getVariable(String name);

    /**
     * Returns the value of the variable. Throws exception if not found.
     *
     * @param name variable name.
     * @param cls variable class.
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(String name, Class<T> cls);

    /**
     * Returns {@code true} if a variable named {@code name} exists.
     *
     * @param name variable name.
     * @return {@code true} if a variable named {@code name} exists.
     */
    boolean existsVariable(String name);

    /**
     * Creates a new processor instance.
     *
     * @param processorClassName processor class name.
     * @return a new processor instance.
     * @param javaClass processor class.
     * @param <T> processor type.
     */
    @SuppressWarnings("rawtypes")
    <T extends Processor> T createProcessorInstance(String processorClassName, Class<T> javaClass);

    /**
     * Creates a plugin instance.
     *
     * @param pluginClassName a plugin class name.
     * @return the plugin instance.
     */
    Plugin createPluginInstance(String pluginClassName);

    /**
     * Returns event specification for rule defined in the knowledge base.
     *
     * @param eventSpecEntry event specification entry.
     * @return event specification.
     */
    RuleEventSpec getRuleEventSpec(Object eventSpecEntry);

    /**
     * Verifies if the given exception is a knowledge base specific one.
     *
     * @param exception an exception to be verified.
     * @return is the exception knowledge base specific.
     */
    boolean isKnowledgeBaseException(Throwable exception);

    /**
     * Returns a Java exception corresponding to the knowledge base specific exception.
     *
     * @param knowledgeBaseException the knowledge base specific exception.
     * @return Java exception.
     */
    Throwable getJavaException(Throwable knowledgeBaseException);

    /**
     * Returns script knowledge base processor class name.
     *
     * @param processorClass processor class.
     * @return script knowledge base processor class name or {@code null} if {@code processorClass} doesn't represent a script knowledge
     *         base processor.
     */
    String getScriptKnowledgeBaseProcessorClassName(Object processorClass);

    /**
     * Returns engine operations associated with this knowledge base interpreter.
     *
     * @return engine operations associated with this knowledge base interpreter.
     */
    KnowledgeBaseEngineOperations getEngineOperations();
}
