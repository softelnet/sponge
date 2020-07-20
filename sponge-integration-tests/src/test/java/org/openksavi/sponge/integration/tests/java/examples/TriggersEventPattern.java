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
import java.util.stream.Stream;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Triggers - Event pattern.
 */
public class TriggersEventPattern extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("countA", new AtomicInteger(0));
        getSponge().setVariable("countAPattern", new AtomicInteger(0));
    }

    public static class TriggerA extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("a");
        }

        @Override
        public void onRun(Event event) {
            getSponge().getVariable(AtomicInteger.class, "countA").incrementAndGet();
        }
    }

    public static class TriggerAPattern extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("a.*");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received matching event {}", event.getName());
            getSponge().getVariable(AtomicInteger.class, "countAPattern").incrementAndGet();
        }
    }

    @Override
    public void onStartup() {
        Stream.of("a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1").forEach(name -> getSponge().event(name).send());
    }
}
