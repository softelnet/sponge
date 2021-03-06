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

package org.openksavi.sponge;

import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Processor operations.
 */
public interface ProcessorOperations {

    /**
     * Returns the knowledge base associated with this processor.
     *
     * @return the knowledge base.
     */
    KnowledgeBase getKnowledgeBase();

    /**
     * Returns the metadata.
     *
     * @return the metadata.
     */
    ProcessorMeta getMeta();
}
