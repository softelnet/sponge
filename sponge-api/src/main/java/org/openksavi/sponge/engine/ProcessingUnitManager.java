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

import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;

/**
 * Processing Unit Manager.
 */
public interface ProcessingUnitManager extends EngineModule {

    /**
     * Returns the Filter Processing Unit.
     *
     * @return the Filter Processing Unit.
     */
    FilterProcessingUnit getFilterProcessingUnit();

    /**
     * Sets the Filter Processing Unit.
     *
     * @param filterProcessingUnit the Filter Processing Unit.
     */
    void setFilterProcessingUnit(FilterProcessingUnit filterProcessingUnit);

    /**
     * Returns the Main Processing Unit.
     *
     * @return the Main Processing Unit.
     */
    MainProcessingUnit getMainProcessingUnit();

    /**
     * Sets the Main Processing Unit.
     *
     * @param mainProcessingUnit the Main Processing Unit.
     */
    void setMainProcessingUnit(MainProcessingUnit mainProcessingUnit);
}
