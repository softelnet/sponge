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

package org.openksavi.sponge;

import org.openksavi.sponge.engine.ProcessorType;

/**
 * An exception thrown when a processor is not registered.
 */
public class ProcessorNotFoundException extends SpongeException {

    private static final long serialVersionUID = -1460515647259232927L;

    private static final String DEFAULT_MESSAGE_PATTERN = "%s %s not found";

    private ProcessorType processorType;

    private String processorName;

    public ProcessorNotFoundException(ProcessorType processorType, String processorName) {
        super(String.format(DEFAULT_MESSAGE_PATTERN, processorType.getLabel(), processorName));

        this.processorType = processorType;
        this.processorName = processorName;
    }

    public ProcessorType getProcessorType() {
        return processorType;
    }

    public String getProcessorName() {
        return processorName;
    }
}
