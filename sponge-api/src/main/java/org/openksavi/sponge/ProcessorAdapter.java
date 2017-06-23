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

package org.openksavi.sponge;

import org.openksavi.sponge.engine.ProcessorType;

/**
 * Processors adapter.
 */
public interface ProcessorAdapter<T extends Processor<?>> extends ProcessorOperations {

    /**
     * Sets processor for this adapter.
     *
     * @param processor processor.
     */
    void setProcessor(T processor);

    /**
     * Returns processor for this adapter.
     *
     * @return processor.
     */
    T getProcessor();

    /**
     * Returns processor definition.
     *
     * @return processor definition.
     */
    ProcessorDefinition getDefinition();

    /**
     * Returns processor type.
     *
     * @return processor type.
     */
    ProcessorType getType();

    /**
     * Clears this processor.
     */
    void clear();

    /**
     * Validates this processor adapter.
     */
    void validate();
}
