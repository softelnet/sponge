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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.QueueFullException;

public class EventOverloadTest {

    @Test
    public void testEventOverload() {
        EngineBuilder<DefaultEngine> builder = DefaultEngine.builder().knowledgeBase("kb", "examples/core/event_overload.py");
        builder.getEngineDefaultParameters().setMainEventQueueCapacity(2);
        builder.getEngineDefaultParameters().setDecomposedQueueCapacity(2);

        Engine engine = builder.build();

        // Caution: this test depends on the exact configuration values as they are specified below.
        engine.getConfigurationManager().setMainProcessingUnitThreadCount(1);
        engine.getConfigurationManager().setEventQueueCapacity(5);
        // engine.getConfigurationManager().setEventSetProcessorDefaultSynchronous(false);
        engine.startup();

        try {
            await().atMost(120, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("testStatus") != null);

            assertTrue(engine.getOperations().getVariable("testStatus") instanceof QueueFullException);
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
