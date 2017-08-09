/*
 * Copyright 2016-2017 Softelnet.
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
import org.openksavi.sponge.java.JavaKnowledgeBase;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.test.util.EventsLog;

public class TestKnowledgeBase extends JavaKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(TestKnowledgeBase.class);

    private EventsLog eventsLog = new EventsLog("e1", "e1e2-first", "e1e2-last", "e1e2-all", "e3");

    @Override
    public void onInit() {
        logger.debug("onInit");

        getEps().setVariable(EventsLog.VARIABLE_NAME, eventsLog);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad() {
        logger.debug("onLoad");

        getEps().enableAll(TestFilter.class, TestTrigger.class, NotToBeRunTrigger.class, TestFirstRule.class, TestLastRule.class,
                TestAllRule.class);
    }

    @Override
    public void onStartup() {
        logger.debug("onStartup");

        getEps().event("e1").set("mark", 1).sendAfter(100);
        getEps().event("e2").set("mark", 2).sendAfter(200);
        getEps().event("e1").set("mark", 3).sendAfter(300);
        getEps().event("e2").set("mark", 4).sendAfter(400);
        getEps().event("e2").set("mark", 5).sendAfter(500);
        getEps().event("e2").set("mark", 6).sendAfter(600);
        getEps().event("e3").sendAfter(1000, 100);
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
    public static class TestFilter extends org.openksavi.sponge.java.JavaFilter {

        @Override
        public void onConfigure() {
            setEvents("e3");
        }

        @Override
        public boolean onAccept(Event event) {
            return false;
        }
    }

    public static class NotToBeRunTrigger extends org.openksavi.sponge.java.JavaTrigger {

        @Override
        public void onConfigure() {
            setEventName("e3");
        }

        @Override
        public void onRun(Event event) {
            EventsLog.getInstance(getEps()).addEvent("e3", event);
            throw new SpongeException("Should not be fired!");
        }
    }

    public static class TestTrigger extends org.openksavi.sponge.java.JavaTrigger {

        @Override
        public void onConfigure() {
            setEventName("e1");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Run");
            EventsLog.getInstance(getEps()).addEvent("e1", event);
        }
    }

    public static class TestLastRule extends org.openksavi.sponge.java.JavaRule {

        @Override
        public void onConfigure() {
            setEvents(new Object[] { makeEventSpec("e1"), makeEventSpec("e2", EventMode.LAST) });
            setConditions("e2", "e2condition");
            setDuration(Duration.ofSeconds(2));
        }

        public boolean e2condition(Event event) {
            return true;
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getEps()).addEvent("e1e2-last", event);
        }
    }

    public static class TestFirstRule extends org.openksavi.sponge.java.JavaRule {

        @Override
        public void onConfigure() {
            setEvents(new Object[] { makeEventSpec("e1"), makeEventSpec("e2", EventMode.FIRST) });
            setDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getEps()).addEvent("e1e2-first", event);
        }
    }

    public static class TestAllRule extends org.openksavi.sponge.java.JavaRule {

        @Override
        public void onConfigure() {
            setEvents(new Object[] { makeEventSpec("e1"), makeEventSpec("e2", EventMode.ALL) });
            setDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("{}-RUN: initial event={}; second event={}", hashCode(), getEvent("e1"), event);
            EventsLog.getInstance(getEps()).addEvent("e1e2-all", event);
        }
    }
}
