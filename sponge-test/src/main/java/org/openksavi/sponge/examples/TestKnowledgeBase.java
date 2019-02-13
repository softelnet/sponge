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

package org.openksavi.sponge.examples;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JFilter;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.java.JTrigger;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.test.util.EventsLog;

public class TestKnowledgeBase extends JKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(TestKnowledgeBase.class);

    private EventsLog eventsLog = new EventsLog("e1", "e1e2-first", "e1e2-last", "e1e2-all", "e3");

    public TestKnowledgeBase(String name) {
        super(name);
    }

    public TestKnowledgeBase() {
        //
    }

    @Override
    public void onInit() {
        logger.debug("onInit");

        getSponge().setVariable(EventsLog.VARIABLE_NAME, eventsLog);
    }

    @Override
    public void onLoad() {
        logger.debug("onLoad");
    }

    @Override
    public void onStartup() {
        logger.debug("onStartup");

        getSponge().event("e1").set("mark", 1).sendAfter(100);
        getSponge().event("e2").set("mark", 2).sendAfter(200);
        getSponge().event("e1").set("mark", 3).sendAfter(300);
        getSponge().event("e2").set("mark", 4).sendAfter(400);
        getSponge().event("e2").set("mark", 5).sendAfter(500);
        getSponge().event("e2").set("mark", 6).sendAfter(600);
        getSponge().event("e3").sendAfter(1000, 100);
    }

    @Override
    public void onClear() {
        logger.debug("onClear");
    }

    @Override
    public void onShutdown() {
        logger.debug("onShutdown");
    }

    /**
     * Reject all events that have name "e3".
     */
    public static class TestFilter extends JFilter {

        @Override
        public void onConfigure() {
            withEvents("e3");
        }

        @Override
        public boolean onAccept(Event event) {
            return false;
        }
    }

    public static class NotToBeRunTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e3");
        }

        @Override
        public void onRun(Event event) {
            EventsLog.getInstance(getSponge()).addEvent("e3", event);
            throw new SpongeException("Should not be fired!");
        }
    }

    public static class TestTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Run");
            EventsLog.getInstance(getSponge()).addEvent("e1", event);
        }
    }

    public static class TestLastRule extends JRule {

        @Override
        public void onConfigure() {
            withEventSpecs(makeEventSpec("e1"), makeEventSpec("e2", EventMode.LAST));
            withConditions("e2", "e2condition");
            withDuration(Duration.ofSeconds(2));
        }

        public boolean e2condition(Event event) {
            return true;
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getSponge()).addEvent("e1e2-last", event);
        }
    }

    public static class TestFirstRule extends JRule {

        @Override
        public void onConfigure() {
            withEventSpecs(makeEventSpec("e1"), makeEventSpec("e2", EventMode.FIRST));
            withDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getSponge()).addEvent("e1e2-first", event);
        }
    }

    public static class TestAllRule extends JRule {

        @Override
        public void onConfigure() {
            withEventSpecs(makeEventSpec("e1"), makeEventSpec("e2", EventMode.ALL));
            withDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getSponge()).addEvent("e1e2-all", event);
        }
    }
}
