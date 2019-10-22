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

import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTriggerBuilder;

/**
 * Sponge Knowledge base. Trigger builders.
 */
public class TriggersBuilder extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("receivedEventA", new AtomicBoolean(false));
        getSponge().setVariable("receivedEventBCount", new AtomicInteger(0));
    }

    @Override
    public void onLoad() {
        getSponge().enable(new JTriggerBuilder("TriggerA").withEvent("a").withOnRun((trigger, event) -> {
            getLogger().debug("Received event {}", event);
            getSponge().getVariable(AtomicBoolean.class, "receivedEventA").set(true);
        }));

        getSponge().enable(new JTriggerBuilder("TriggerB").withEvent("b").withOnRun((trigger, event) -> {
            trigger.getLogger().debug("Received event {}", event);
            AtomicInteger receivedEventBCount = getSponge().getVariable(AtomicInteger.class, "receivedEventBCount");
            if (receivedEventBCount.get() == 0) {
                trigger.getLogger().debug("Statistics: {}", getSponge().getStatisticsSummary());
            }
            receivedEventBCount.incrementAndGet();
        }));
    }

    @Override
    public void onStartup() {
        getLogger().debug("Startup {}, triggers: {}", getSponge().getInfo(), getSponge().getEngine().getTriggers());
        getLogger().debug("Knowledge base name: {}", getSponge().getKb().getName());
        getSponge().event("a").send();
        getSponge().event("b").sendAfter(200, 200);
    }
}
