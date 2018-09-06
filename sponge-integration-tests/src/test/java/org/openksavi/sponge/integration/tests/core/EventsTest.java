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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;

public class EventsTest {

    @Test
    public void testSendSameEvent() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        engine.startup();
        Event event = engine.getOperations().event("e").set("value", 1).make();
        engine.getOperations().event(event).send();

        try {
            engine.getOperations().event(event).send();
        } catch (SpongeException e) {
            assertEquals(String.format("The event with id %s has already been sent", event.getId()), e.getMessage());
        }
    }
}
