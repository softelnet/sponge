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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.core.engine.processing.decomposed.DecomposedQueueMainProcessingUnit;
import org.openksavi.sponge.engine.Engine;

public class ShutdownTest {

    @Test
    public void testShutdown1() {
        testShutdown(1);
    }

    @Test
    public void testShutdow5() {
        testShutdown(5);
    }

    private void testShutdown(int sizes) {
        EngineBuilder<DefaultEngine> builder = DefaultEngine.builder().knowledgeBase("kb", "examples/core/shutdown.py");

        // Caution: this test depends on the exact configuration values as they are specified below.

        builder.getEngineDefaultParameters().setEventQueueCapacity(1000);
        builder.getEngineDefaultParameters().setExecutorShutdownTimeout(60000);

        int eventsToBeProcessed = 0;

        // One event waits in the FilerProcessingUnit for putting into the Main Event Queue.
        eventsToBeProcessed++;

        // Some events wait in the Main Event Queue.
        builder.getEngineDefaultParameters().setMainEventQueueCapacity(sizes);
        eventsToBeProcessed += builder.getEngineDefaultParameters().getMainEventQueueCapacity();

        // One event waits in the Main Processing Unit for putting into the Decomposed Queue.
        eventsToBeProcessed++;

        // Some events wait in the Decomposed Queue.
        builder.getEngineDefaultParameters().setDecomposedQueueCapacity(sizes);
        eventsToBeProcessed += builder.getEngineDefaultParameters().getDecomposedQueueCapacity();

        // One event waits in the DecomposedQueueReaderWorker for putting into the WorkerThreadPool executor queue.
        eventsToBeProcessed++;

        // Some events wait in the the WorkerThreadPool executor queue.
        builder.getEngineDefaultParameters().setMainProcessingUnitWorkerExecutorQueueSize(sizes);
        eventsToBeProcessed += builder.getEngineDefaultParameters().getMainProcessingUnitWorkerExecutorQueueSize();

        // Some events are being processed in the WorkerThreadPool. Note that for this test assertions there should be only one thread here.
        builder.getEngineDefaultParameters().setMainProcessingUnitThreadCount(1);
        eventsToBeProcessed += builder.getEngineDefaultParameters().getMainProcessingUnitThreadCount();

        Engine engine = builder.build();

        engine.startup();

        try {
            TimeUnit.SECONDS.sleep(2);
            engine.shutdown();

            final int finalEventsToBeProcessed = eventsToBeProcessed;

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "finishedEvents").intValue() >= finalEventsToBeProcessed);

            assertEquals(eventsToBeProcessed, engine.getOperations().getVariable(Number.class, "finishedEvents").intValue());
            assertEquals(engine.getOperations().getVariable(Number.class, "sentEvents").intValue() - finalEventsToBeProcessed,
                    engine.getEventQueueManager().getInputEventQueue().getSize());
            assertEquals(0, engine.getEventQueueManager().getMainEventQueue().getSize());

            DecomposedQueueMainProcessingUnit mainProcessingUnit =
                    (DecomposedQueueMainProcessingUnit) engine.getProcessingUnitManager().getMainProcessingUnit();
            assertEquals(0, mainProcessingUnit.getDecomposedQueue().getSize());
        } catch (InterruptedException e) {
            fail(e.toString());
        } finally {
            engine.shutdown();
        }
    }
}
