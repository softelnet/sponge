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
import java.util.concurrent.atomic.AtomicBoolean;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JFilter;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.java.JTrigger;
import org.openksavi.sponge.rule.Rule;

/**
 * Sponge Knowledge base. Heartbeat.
 */
public class RulesHeartbeat extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("soundTheAlarm", new AtomicBoolean(false));
    }

    public static class HeartbeatFilter extends JFilter {

        private int heartbeatCounter = 0;

        @Override
        public void onConfigure() {
            withEvent("heartbeat");
        }

        @Override
        public boolean onAccept(Event event) {
            heartbeatCounter++;
            if (heartbeatCounter > 2) {
                getSponge().removeEvent(getSponge().getVariable("hearbeatEventEntry"));
                return false;
            } else {
                return true;
            }
        }
    }

    /** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
    public static class HeartbeatRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("heartbeat h1", "heartbeat h2 :none");
            withCondition("h2", (Rule rule, Event event) -> rule.getFirstEvent().get("source").equals(event.get("source")));
            withDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getSponge().event("alarm").set("severity", 1).send();
        }
    }

    public static class AlarmTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("alarm");
        }

        @Override
        public void onRun(Event event) {
            System.out.println("Sound the alarm!");
            getSponge().getVariable(AtomicBoolean.class, "soundTheAlarm").set(true);
        }
    }

    @Override
    public void onStartup() {
        getSponge().setVariable("hearbeatEventEntry", getSponge().event("heartbeat").set("source", "Host1").sendAfter(100, 1000));
    }
}
