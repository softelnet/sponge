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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JCorrelator;
import org.openksavi.sponge.java.JKnowledgeBase;

/**
 * Sponge Knowledge base. Using correlator duration
 */
public class CorrelatorsDuration extends JKnowledgeBase {

    @Override
    public void onInit() {
        getSponge().setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
        getSponge().setVariable("instanceStarted", new AtomicBoolean(false));
    }

    public static class SampleCorrelator extends JCorrelator {

        private List<Event> eventLog = new ArrayList<>();

        @Override
        public void onConfigure() {
            withEvents("filesystemFailure", "diskFailure").withDuration(Duration.ofSeconds(2));
        }

        @Override
        public boolean onAcceptAsFirst(Event event) {
            return getSponge().getVariable(AtomicBoolean.class, "instanceStarted").compareAndSet(false, true);
        }

        @Override
        public void onEvent(Event event) {
            eventLog.add(event);
            getSponge().getVariable(AtomicInteger.class, "hardwareFailureScriptCount").incrementAndGet();
        }

        @Override
        public void onDuration() {
            getLogger().debug("{} - log: {}", hashCode(), eventLog);
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("filesystemFailure").set("source", "server1").send();
        getSponge().event("diskFailure").set("source", "server1").sendAfter(200, 100);
        getSponge().event("diskFailure").set("source", "server2").sendAfter(200, 100);
    }
}
