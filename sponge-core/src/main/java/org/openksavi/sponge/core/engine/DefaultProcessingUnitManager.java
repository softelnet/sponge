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

package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessingUnitManager;
import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;

/**
 * Processing Unit Manager.
 */
public class DefaultProcessingUnitManager extends BaseEngineModule implements ProcessingUnitManager {

    /** The Filter Processing Unit. */
    private FilterProcessingUnit filterProcessingUnit;

    /** The Main Processing Unit. */
    private MainProcessingUnit mainProcessingUnit;

    /**
     * Creates a new Processing Unit Manager.
     *
     * @param engine the engine.
     */
    public DefaultProcessingUnitManager(Engine engine) {
        super("ProcessingUnitManager", engine);
    }

    @Override
    public FilterProcessingUnit getFilterProcessingUnit() {
        return filterProcessingUnit;
    }

    @Override
    public void setFilterProcessingUnit(FilterProcessingUnit filterProcessingUnit) {
        this.filterProcessingUnit = filterProcessingUnit;
    }

    @Override
    public MainProcessingUnit getMainProcessingUnit() {
        return mainProcessingUnit;
    }

    @Override
    public void setMainProcessingUnit(MainProcessingUnit mainProcessingUnit) {
        this.mainProcessingUnit = mainProcessingUnit;
    }

    @Override
    public void doStartup() {
        mainProcessingUnit.startup();
        filterProcessingUnit.startup();
    }

    @Override
    public void doShutdown() {
        filterProcessingUnit.shutdown();
        mainProcessingUnit.shutdown();
    }
}
