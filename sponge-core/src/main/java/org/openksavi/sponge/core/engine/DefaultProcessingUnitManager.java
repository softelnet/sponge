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

package org.openksavi.sponge.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessingUnitManager;
import org.openksavi.sponge.engine.processing.ProcessingUnit;

/**
 * Processing Unit Manager.
 */
public class DefaultProcessingUnitManager extends BaseEngineModule implements ProcessingUnitManager {

    /** The list of processing units. */
    private List<ProcessingUnit<?>> processingUnits = Collections.synchronizedList(new ArrayList<>());

    /**
     * Creates a new Processing Unit Manager.
     *
     * @param engine the engine.
     */
    public DefaultProcessingUnitManager(Engine engine) {
        super("ProcessingUnitManager", engine);
    }

    /**
     * Returns processing units.
     *
     * @return processing units.
     */
    @Override
    public List<ProcessingUnit<?>> getProcessingUnits() {
        return processingUnits;
    }

    /**
     * Adds a new processing unit.
     *
     * @param processingUnit processing unit.
     */
    @Override
    public void addProcessingUnit(ProcessingUnit<?> processingUnit) {
        processingUnits.add(processingUnit);
    }

    @Override
    public void startup() {
        processingUnits.forEach(processingUnit -> processingUnit.startup());

        setRunning(true);
    }

    @Override
    public void shutdown() {
        if (!isRunning()) {
            return;
        }

        super.shutdown();

        processingUnits.forEach(processingUnit -> processingUnit.shutdown());
    }

    /**
     * Clears processing units. Removes all processors registered in all processing units.
     */
    @Override
    public void clear() {
        synchronized (processingUnits) {
            processingUnits.forEach(processingUnit -> processingUnit.removeAllProcessors());
        }
    }
}
