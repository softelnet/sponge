/*
 * Copyright 2016-2018 The Sponge authors.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JTriggerBuilder;

public class EventsTest {

    @Test
    public void testSendSameEvent() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        engine.startup();

        try {
            Event event = engine.getOperations().event("e").set("value", 1).make();
            engine.getOperations().event(event).send();

            try {
                engine.getOperations().event(event).send();

                fail("Exception expected");
            } catch (IllegalArgumentException e) {
                assertEquals(String.format("The event with id %s has already been sent", event.getId()), e.getMessage());
            }
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEventNameIncorrectWhitespace() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        engine.startup();

        try {
            assertThrows(IllegalArgumentException.class, () -> engine.getOperations().event("e ").send());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEventNameIncorrectColon() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        engine.startup();

        try {
            assertThrows(IllegalArgumentException.class, () -> engine.getOperations().event("e:1").send());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEventFeatures() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        engine.startup();

        try {
            Event event = engine.getOperations().event("e").set("value", 1).feature("icon", "alarm").make();

            AtomicReference<Event> eventHolder = new AtomicReference<>();

            engine.getOperations()
                    .enable(new JTriggerBuilder("TriggerA").withEvent(event.getName()).withOnRun((trigger, e) -> eventHolder.set(e)));

            engine.getOperations().event(event).send();

            await().atMost(10, TimeUnit.SECONDS).until(() -> eventHolder.get() != null);

            assertEquals("alarm", eventHolder.get().getFeatures().get("icon"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
