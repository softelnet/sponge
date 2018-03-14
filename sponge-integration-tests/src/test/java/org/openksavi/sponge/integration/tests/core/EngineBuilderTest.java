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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.examples.EchoPlugin;
import org.openksavi.sponge.examples.TestKnowledgeBase;
import org.openksavi.sponge.test.util.EventsLog;

public class EngineBuilderTest {

    private SpongeEngine createAndStartupEngine() {
        EchoPlugin plugin = new EchoPlugin();
        plugin.setName("testPlugin");
        plugin.setEcho("Echo text!");

        //@formatter:off
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .systemProperty("system.property", "1")
                .property("test.property", "TEST")
                .plugin(plugin)
                .knowledgeBase(new TestKnowledgeBase("testJavaKb"))
                .build();
        //@formatter:on

        engine.getConfigurationManager().setMainProcessingUnitThreadCount(2);
        engine.getConfigurationManager().setEventClonePolicy(EventClonePolicy.DEEP);

        engine.startup();

        return engine;
    }

    private List<Event> getEvents(SpongeEngine engine, String key) {
        return EventsLog.getInstance(engine.getOperations()).getEvents(key);
    }

    @Test
    public void testEngineBuilder() throws InterruptedException {
        SpongeEngine engine = createAndStartupEngine();

        try {
            await().pollDelay(3, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> getEvents(engine, "e1").size() >= 1 && getEvents(engine, "e1e2-first").size() >= 2
                            && getEvents(engine, "e1e2-last").size() >= 2 && getEvents(engine, "e1e2-all").size() >= 7);

            assertEquals(2, getEvents(engine, "e1").size());
            assertEquals(2, getEvents(engine, "e1e2-first").size());
            assertEquals(2, getEvents(engine, "e1e2-last").size());
            assertEquals(7, getEvents(engine, "e1e2-all").size());
            assertEquals(0, getEvents(engine, "e3").size());

            assertEquals(2, getEvents(engine, "e1e2-first").get(0).<Number>get("mark"));
            assertEquals(4, getEvents(engine, "e1e2-first").get(1).<Number>get("mark"));

            assertEquals(6, getEvents(engine, "e1e2-last").get(0).<Number>get("mark"));
            assertEquals(6, getEvents(engine, "e1e2-last").get(1).<Number>get("mark"));

            assertEquals(2, getEvents(engine, "e1e2-all").get(0).<Number>get("mark"));
            assertEquals(4, getEvents(engine, "e1e2-all").get(1).<Number>get("mark"));
            assertEquals(4, getEvents(engine, "e1e2-all").get(2).<Number>get("mark"));
            assertEquals(5, getEvents(engine, "e1e2-all").get(3).<Number>get("mark"));
            assertEquals(5, getEvents(engine, "e1e2-all").get(4).<Number>get("mark"));
            assertEquals(6, getEvents(engine, "e1e2-all").get(5).<Number>get("mark"));
            assertEquals(6, getEvents(engine, "e1e2-all").get(6).<Number>get("mark"));
        } finally {
            engine.shutdown();
        }
        if (engine.isError()) {
            throw SpongeUtils.wrapException(engine.getError());
        }
    }
}
