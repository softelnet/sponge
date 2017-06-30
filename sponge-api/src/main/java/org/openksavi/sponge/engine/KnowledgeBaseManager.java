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

package org.openksavi.sponge.engine;

import java.util.List;

import org.openksavi.sponge.config.Configurable;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseOperations;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;

/**
 * Knowledge Base Manager.
 */
public interface KnowledgeBaseManager extends Configurable, EngineModule, KnowledgeBaseOperations {

    /**
     * Sets knowledge base interpreter factory providers.
     *
     * @param providers knowledge base interpreter factory providers.
     */
    void setKnowledgeBaseInterpreterFactoryProviders(List<KnowledgeBaseInterpreterFactoryProvider> providers);

    /**
     * Reloads knowledge bases.
     */
    void reload();

    /**
     * Adds a knowledge base to this knowledge base manager.
     *
     * @param knowledgeBase knowledge base.
     */
    void addKnowledgeBase(KnowledgeBase knowledgeBase);

    /**
     * Returns a knowledge base that has the specified name. Throws IllegalArgumentException if none has been found.
     *
     * @param name a knowledge base name.
     * @return a knowledge base.
     */
    KnowledgeBase getKnowledgeBase(String name);

    /**
     * Returns a script knowledge base that has the specified name. Throws IllegalArgumentException if none has been found.
     *
     * @param name a script knowledge base name.
     * @return a script knowledge base.
     */
    ScriptKnowledgeBase getScriptKnowledgeBase(String name);

    /**
     * Returns the default knowledge base.
     *
     * @return the default knowledge base.
     */
    KnowledgeBase getDefaultKnowledgeBase();

    /**
     * Returns the main knowledge base. This is the first declared knowledge base.
     *
     * @return the main knowledge base.
     */
    KnowledgeBase getMainKnowledgeBase();

    /**
     * Returns all knowledge bases.
     *
     * @return knowledge bases.
     */
    List<KnowledgeBase> getKnowledgeBases();

    /**
     * Unwraps (if possible) the given exception.
     *
     * @param exception exception to unwrap.
     * @return unwrapped exception or {@code null} if the unwrapping wasn't possible.
     */
    Throwable unwrapKnowledgeBaseException(Throwable exception);

    /**
     * Sets the global variable in all interpreters in all knowledge bases.
     *
     * @param name variable name.
     * @param value variable value.
     */
    void setInterpreterGlobalVariable(String name, Object value);
}
