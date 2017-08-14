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

package org.openksavi.sponge.core;

import org.openksavi.sponge.ProcessorOperations;
import org.openksavi.sponge.core.kb.BaseKnowledgeBase;

public interface ProcessorAdapterImplementor extends ProcessorOperations {

    /**
     * Sets the knowledge base associated with this processor.
     *
     * @param knowledgeBase the knowledge base.
     */
    void setKnowledgeBase(BaseKnowledgeBase knowledgeBase);

    /**
     * Returns Java-defined flag.
     *
     * @return Java-defined flag.
     */
    boolean isJavaDefined();

    /**
     * Sets processor definition.
     *
     * @param processorDef processor definition.
     */
    void setDefinition(BaseProcessorDefinition processorDef);

    /**
     * Returns processor definition.
     *
     * @return processor definition.
     */
    BaseProcessorDefinition getDefinition();
}
