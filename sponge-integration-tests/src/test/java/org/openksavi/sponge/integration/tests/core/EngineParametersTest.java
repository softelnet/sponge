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

package org.openksavi.sponge.integration.tests.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.EventClonePolicy;

public class EngineParametersTest {

    @Test
    public void testEngineParameters() throws InterruptedException {
        Engine engine = DefaultEngine.builder().config("examples/core/engine_parameters.xml").build();
        engine.startup();

        try {
            assertEquals("EngineParametersTest", engine.getName());
            assertEquals(21, engine.getConfigurationManager().getMainProcessingUnitThreadCount());
            assertEquals(engine.getConfigurationManager().getMainProcessingUnitThreadCount(),
                    engine.getConfigurationManager().getAsyncEventSetProcessorExecutorThreadCount());
            assertEquals(EventClonePolicy.DEEP, engine.getConfigurationManager().getEventClonePolicy());
            assertEquals(false, engine.getConfigurationManager().getAutoEnable());
        } finally {
            engine.shutdown();
        }
    }
}