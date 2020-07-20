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
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JCorrelator;
import org.openksavi.sponge.java.JFilter;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Auto-enable.
 */
public class KnowledgeBaseAutoEnable extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("counter", new AtomicInteger(0));
    }

    public static class AutoAction extends JAction {

        public Object onCall() {
            getLogger().debug("Running");
            getSponge().getVariable(AtomicInteger.class, "counter").incrementAndGet();
            return null;
        }
    }

    public static class AutoFilter extends JFilter {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public boolean onAccept(Event event) {
            getLogger().debug("Received event: {}", event.getName());
            getSponge().getVariable(AtomicInteger.class, "counter").incrementAndGet();
            return true;
        }
    }

    public static class AutoTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received event: {}", event.getName());
            getSponge().getVariable(AtomicInteger.class, "counter").incrementAndGet();
        }
    }

    public static class AutoRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running for sequence: {}", getEventSequence());
            getSponge().getVariable(AtomicInteger.class, "counter").incrementAndGet();
        }
    }

    public static class AutoCorrelator extends JCorrelator {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2");
        }

        @Override
        public boolean onAcceptAsFirst(Event event) {
            return event.getName().equals("e1");
        }

        @Override
        public void onEvent(Event event) {
            getLogger().debug("Received event: {}", event.getName());
            if (event.getName().equals("e2")) {
                getSponge().getVariable(AtomicInteger.class, "counter").incrementAndGet();
                finish();
            }
        }
    }

    @Override
    public void onStartup() {
        getSponge().call("AutoAction");
        getSponge().event("e1").send();
        getSponge().event("e2").send();
    }
}
