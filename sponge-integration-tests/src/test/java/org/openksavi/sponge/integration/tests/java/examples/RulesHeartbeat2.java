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
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;

/**
 * Sponge Knowledge base. Heartbeat 2.
 */
public class RulesHeartbeat2 extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("soundTheAlarm", new AtomicBoolean(false));
    }

    /** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
    public static class HeartbeatRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("heartbeat h1", "heartbeat h2 :none").withDuration(Duration.ofSeconds(2));
        }

        @Override
        public void onRun(Event event) {
            getLogger().info("Sound the alarm!");
            getSponge().getVariable(AtomicBoolean.class, "soundTheAlarm").set(true);
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("heartbeat").send();
        getSponge().event("heartbeat").sendAfter(1000);
    }
}
