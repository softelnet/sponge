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

import org.openksavi.sponge.engine.processing.ProcessingUnit;

/**
 * Processing Unit Manager.
 */
public interface ProcessingUnitManager extends EngineModule {

    /**
     * Returns processing units.
     *
     * @return processing units.
     */
    List<ProcessingUnit<?>> getProcessingUnits();

    /**
     * Adds a new processing unit.
     *
     * @param processingUnit processing unit.
     */
    void addProcessingUnit(ProcessingUnit<?> processingUnit);

    /**
     * Clears processing units. Removes all processors registered in all processing units.
     */
    void clear();
}
