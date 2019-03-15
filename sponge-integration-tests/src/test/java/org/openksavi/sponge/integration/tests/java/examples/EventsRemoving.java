/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.integration.tests.java.examples;

import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge base. Removing scheduled events.
 */
public class EventsRemoving extends JKnowledgeBase {

    @Override
    public void onInit() {
        getSponge().setVariable("eventCounter", new AtomicInteger(0));
        getSponge().setVariable("allowNumber", 2);
    }

    public static class Trigger1 extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public void onRun(Event event) {
            AtomicInteger eventCounter = getSponge().getVariable(AtomicInteger.class, "eventCounter");
            eventCounter.incrementAndGet();
            getLogger().debug("Received event {}, counter: {}", event.getName(), eventCounter);
            if (eventCounter.get() > getSponge().getVariable(Number.class, "allowNumber").intValue()) {
                getLogger().debug("This line should not be displayed!");
            }
        }
    }

    public static class Trigger2 extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e2");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Removing entry");
            getSponge().removeEvent(getSponge().getVariable("eventEntry"));
        }
    }

    @Override
    public void onStartup() {
        long start = 500;
        long interval = 1000;
        getSponge().setVariable("eventEntry", getSponge().event("e1").sendAfter(start, interval));
        getSponge().event("e2").sendAfter(interval * getSponge().getVariable(Number.class, "allowNumber").intValue());
    }
}
