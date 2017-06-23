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

package org.openksavi.sponge.examples.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.TestUtils;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class EventsTestTemplate {

    @SuppressWarnings("unchecked")
    private static String getValue(List<Event> events, int index) {
        return ((Map<String, String>) events.get(index).get("map")).get("a");
    }

    public static void testClonePolicy(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "events_clone_policy");

        try {
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> TestUtils.getEvents(engine, "defaultClonePolicy").size() >= 3 &&
                            TestUtils.getEvents(engine, "deepClonePolicy").size() >= 3 &&
                            TestUtils.getEvents(engine, "shallowClonePolicy").size() >= 3);

            // Default clone policy is set to DEEP in the configuration xml.
            List<Event> defaultClonePolicyEvents = TestUtils.getEvents(engine, "defaultClonePolicy");
            assertEquals("Value 1", getValue(defaultClonePolicyEvents, 0));
            assertEquals("Value 2", getValue(defaultClonePolicyEvents, 1));
            assertEquals("Value 3", getValue(defaultClonePolicyEvents, 2));

            List<Event> deepClonePolicyEvents = TestUtils.getEvents(engine, "deepClonePolicy");
            assertEquals("Value 1", getValue(deepClonePolicyEvents, 0));
            assertEquals("Value 2", getValue(deepClonePolicyEvents, 1));
            assertEquals("Value 3", getValue(deepClonePolicyEvents, 2));

            List<Event> shallowClonePolicyEvents = TestUtils.getEvents(engine, "shallowClonePolicy");
            assertEquals("Value " + defaultClonePolicyEvents.size(), getValue(shallowClonePolicyEvents, 0));
            assertEquals("Value " + defaultClonePolicyEvents.size(), getValue(shallowClonePolicyEvents, 1));
            assertEquals("Value " + defaultClonePolicyEvents.size(), getValue(shallowClonePolicyEvents, 2));
        } finally {
            engine.shutdown();
        }
    }

    public static void testCron(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "events_cron");

        try {
            await().atMost(15, TimeUnit.SECONDS).until(() -> ((Number) engine.getOperations().getVariable("eventCounter")).intValue() >= 2);
        } finally {
            engine.shutdown();
        }
    }

    public static void testRemovingEvent(KnowledgeBaseType type) throws InterruptedException {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "events_removing");

        try {
            await().pollDelay(3, TimeUnit.SECONDS).atMost(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("eventCounter"))
                            .intValue() == ((Number) engine.getOperations().getVariable("allowNumber")).intValue());
            TimeUnit.SECONDS.sleep(1);
            assertEquals(((Number) engine.getOperations().getVariable("allowNumber")).intValue(),
                    ((Number) engine.getOperations().getVariable("eventCounter")).intValue());
        } finally {
            engine.shutdown();
        }
    }
}
