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
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.examples.EchoPlugin;
import org.openksavi.sponge.examples.TestKnowledgeBase;
import org.openksavi.sponge.test.util.EventsLog;

public class EngineBuilderTest {

    private Engine createAndStartupEngine() {
        EchoPlugin plugin = new EchoPlugin();
        plugin.setName("testPlugin");
        plugin.setEcho("Echo text!");

        //@formatter:off
        Engine engine = DefaultEngine.builder()
                .systemProperty("system.property", "1")
                .property("test.property", "TEST")
                .plugin(plugin)
                .knowledgeBase(new TestKnowledgeBase())
                .build();
        //@formatter:on

        engine.getConfigurationManager().setMainProcessingUnitThreadCount(2);
        engine.getConfigurationManager().setEventClonePolicy(EventClonePolicy.DEEP);

        engine.startup();

        return engine;
    }

    private List<Event> getEvents(Engine engine, String key) {
        return EventsLog.getInstance(engine.getOperations()).getEvents(key);
    }

    @Test
    public void testEngineBuilder() throws InterruptedException {
        Engine engine = createAndStartupEngine();

        await().pollDelay(3, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> getEvents(engine, "e1e2-all").size() >= 7);

        assertEquals(2, getEvents(engine, "e1").size());
        assertEquals(2, getEvents(engine, "e1e2-first").size());
        assertEquals(2, getEvents(engine, "e1e2-last").size());
        assertEquals(7, getEvents(engine, "e1e2-all").size());
        assertEquals(0, getEvents(engine, "e3").size());

        assertEquals(2, getEvents(engine, "e1e2-first").get(0).get("mark"));
        assertEquals(4, getEvents(engine, "e1e2-first").get(1).get("mark"));

        assertEquals(6, getEvents(engine, "e1e2-last").get(0).get("mark"));
        assertEquals(6, getEvents(engine, "e1e2-last").get(1).get("mark"));

        assertEquals(2, getEvents(engine, "e1e2-all").get(0).get("mark"));
        assertEquals(4, getEvents(engine, "e1e2-all").get(1).get("mark"));
        assertEquals(4, getEvents(engine, "e1e2-all").get(2).get("mark"));
        assertEquals(5, getEvents(engine, "e1e2-all").get(3).get("mark"));
        assertEquals(5, getEvents(engine, "e1e2-all").get(4).get("mark"));
        assertEquals(6, getEvents(engine, "e1e2-all").get(5).get("mark"));
        assertEquals(6, getEvents(engine, "e1e2-all").get(6).get("mark"));

        engine.shutdown();
        if (engine.isError()) {
            throw Utils.wrapException("testEngineBuilder", engine.getError());
        }
    }
}
