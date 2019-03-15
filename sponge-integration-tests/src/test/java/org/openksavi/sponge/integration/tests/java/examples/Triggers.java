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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.SampleJavaTrigger;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge base. Triggers - Generating events and using triggers.
 */
public class Triggers extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("receivedEventA", new AtomicBoolean(false));
        getSponge().setVariable("receivedEventBCount", new AtomicInteger(0));
        getSponge().setVariable("receivedEventTestJavaCount", new AtomicInteger(0));
    }

    public static class TriggerA extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("a");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received event {}", event);
            getSponge().getVariable(AtomicBoolean.class, "receivedEventA").set(true);
        }
    }

    public static class TriggerB extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("b");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received event {}", event);
            AtomicInteger receivedEventBCount = getSponge().getVariable(AtomicInteger.class, "receivedEventBCount");
            if (receivedEventBCount.get() == 0) {
                getLogger().debug("Statistics: {}", getSponge().getStatisticsSummary());
            }
            receivedEventBCount.incrementAndGet();
        }
    }

    @Override
    public void onLoad() {
        getSponge().enableJava(SampleJavaTrigger.class);
    }

    @Override
    public void onStartup() {
        getLogger().debug("Startup {}, triggers: {}", getSponge().getInfo(), getSponge().getEngine().getTriggers());
        getLogger().debug("Knowledge base name: {}", getSponge().getKb().getName());
        getSponge().event("a").send();
        getSponge().event("b").sendAfter(200, 200);
        getSponge().event("testJava").send();
    }

    @Override
    public void onShutdown() {
        getLogger().debug("Shutting down");
    }
}
