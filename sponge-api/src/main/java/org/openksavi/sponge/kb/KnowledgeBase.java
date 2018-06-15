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

import org.openksavi.sponge.util.Descriptive;

/**
 * Knowledge base.
 */
public interface KnowledgeBase extends KnowledgeBaseOperations, Descriptive {

    /**
     * Returns knowledge base interpreter type.
     *
     * @return knowledge base interpreter type.
     */
    KnowledgeBaseType getType();

    /**
     * Sets knowledge base interpreter type.
     *
     * @param type knowledge base interpreter type.
     */
    void setType(KnowledgeBaseType type);

    /**
     * Returns engine operations associated with this knowledge base.
     *
     * @return engine operations associated with this knowledge base.
     */
    KnowledgeBaseEngineOperations getEngineOperations();

    /**
     * Returns an interpreter associated with this knowledge base.
     *
     * @return an interpreter associated with this knowledge base.
     */
    KnowledgeBaseInterpreter getInterpreter();

    /**
     * Sets an interpreter associated with this knowledge base.
     *
     * @param knowledgeBaseInterpreter an interpreter associated with this knowledge base.
     */
    void setInterpreter(KnowledgeBaseInterpreter knowledgeBaseInterpreter);

    /**
     * Scans this knowledge base for processor definitions (classes) in order to auto enable.
     */
    void scanToAutoEnable();
}
