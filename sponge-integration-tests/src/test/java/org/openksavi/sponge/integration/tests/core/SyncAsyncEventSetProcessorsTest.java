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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.examples.util.CorrelationEventsLog;
import org.openksavi.sponge.test.util.TestUtils;

public class SyncAsyncEventSetProcessorsTest {

    @Test
    public void testAsyncEventSetProcessors() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/sync_async_event_set_processors.xml").build();
        engine.startup();

        assertTrue(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        doTestAsyncEventSetProcessors(engine);
    }

    @Test
    public void testSyncEventSetProcessorsConfig() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/sync_async_event_set_processors.py").build();
        engine.startup();

        assertFalse(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        doTestAsyncEventSetProcessors(engine);
    }

    private void doTestAsyncEventSetProcessors(SpongeEngine engine) throws InterruptedException {
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
