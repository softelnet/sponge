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

package org.openksavi.sponge.spi;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;

/**
 * Processing Unit provider.
 */
public interface ProcessingUnitProvider {

    /**
     * Creates a new filter processing unit.
     *
     * @param engine an engine.
     * @param inQueue an input event queue for the returned processing unit.
     * @param outQueue an output event queue for the returned processing unit.
     * @return a new filter processing unit.
     */
    FilterProcessingUnit createFilterProcessingUnit(Engine engine, EventQueue inQueue, EventQueue outQueue);

    /**
     * Creates a new main processing unit.
     *
     * @param engine an engine.
     * @param inQueue an input event queue for the returned processing unit.
     * @param outQueue an output event queue for the returned processing unit.
     * @return a new main processing unit.
     */
    MainProcessingUnit createMainProcessingUnit(Engine engine, EventQueue inQueue, EventQueue outQueue);
}
