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
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.test.util.CorrelationEventsLog;
import org.openksavi.sponge.test.util.TestUtils;

public class SyncAsyncEventSetProcessorsTest {

    @Test
    public void testAsyncEventSetProcessors() throws InterruptedException {
        Engine engine = DefaultEngine.builder().config("examples/core/sync_async_event_set_processors.xml").build();
        engine.startup();

        assertTrue(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        doTestAsyncEventSetProcessors(engine);
    }

    @Test
    public void testSyncEventSetProcessorsConfig() throws InterruptedException {
        Engine engine = DefaultEngine.builder().knowledgeBase("kb", "examples/core/sync_async_event_set_processors.py").build();
        engine.startup();

        assertFalse(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        doTestAsyncEventSetProcessors(engine);
    }

    private void doTestAsyncEventSetProcessors(Engine engine) throws InterruptedException {
        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            await().pollDelay(1, TimeUnit.SECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getEvents("RuleFFF", "1").size() >= 1 && eventsLog.getEvents("RuleFFL", "1").size() >= 1);

            TestUtils.assertEventSequences(eventsLog, "RuleFFF", "1", new String[][] { { "1", "2", "5" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFFL", "1", new String[][] { { "1", "2", "7" } });
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
