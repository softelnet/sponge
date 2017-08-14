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

package org.openksavi.sponge.core.spi;

import org.openksavi.sponge.core.engine.processing.DefaultFilterProcessingUnit;
import org.openksavi.sponge.core.engine.processing.decomposed.DecomposedQueueMainProcessingUnit;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.spi.ProcessingUnitProvider;

/**
 * Default processing unit provider.
 */
public class DefaultProcessingUnitProvider implements ProcessingUnitProvider {

    @Override
    public FilterProcessingUnit createFilterProcessingUnit(Engine engine, EventQueue inQueue, EventQueue outQueue) {
        return new DefaultFilterProcessingUnit("FilterProcessingUnit", engine, inQueue, outQueue);
    }

    @Override
    public MainProcessingUnit createMainProcessingUnit(Engine engine, EventQueue inQueue, EventQueue outQueue) {
        return new DecomposedQueueMainProcessingUnit("MainProcessingUnit", engine, inQueue, outQueue);
    }
}
