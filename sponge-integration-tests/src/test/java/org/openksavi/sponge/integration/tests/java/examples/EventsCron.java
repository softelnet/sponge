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
 * Sponge Knowledge Base. Generating events by Cron.
 */
public class EventsCron extends JKnowledgeBase {

    @Override
    public void onInit() {
        getSponge().setVariable("scheduleEntry", null);
        getSponge().setVariable("eventCounter", new AtomicInteger(0));
    }

    public static class CronTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("cronEvent");
        }

        @Override
        public void onRun(Event event) {
            AtomicInteger eventCounter = getSponge().getVariable(AtomicInteger.class, "eventCounter");
            eventCounter.incrementAndGet();
            getLogger().debug("Received event {}: {}", eventCounter.get(), event.getName());
            if (eventCounter.get() == 2) {
                getLogger().debug("removing scheduled event");
                getSponge().removeEvent(getSponge().getVariable("scheduleEntry"));
            }
        }
    }

    @Override
    public void onStartup() {
        // send event every 2 seconds
        getSponge().setVariable("scheduleEntry", getSponge().event("cronEvent").sendAt("0/2 * * * * ?"));
    }
}
