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

import org.slf4j.Logger;

import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;

/**
 * Processor.
 */
public interface Processor<T extends ProcessorAdapter<?>> extends ProcessorOperations {

    /**
     * A configuration callback method that is invoked when a processor is being enabled.
     */
    void onConfigure();

    /**
     * An initialization callback method that is invoked after {@code onConfigure()}, each time a new working instance of the processor is
     * created.
     */
    void onInit();

    /**
     * Returns the adapter.
     *
     * @return the adapter.
     */
    T getAdapter();

    /**
     * Returns the logger.
     *
     * @return the logger.
     */
    Logger getLogger();

    /**
     * Returns the engine operations.
     *
     * @return the engine operations.
     */
    KnowledgeBaseEngineOperations getSponge();
}
