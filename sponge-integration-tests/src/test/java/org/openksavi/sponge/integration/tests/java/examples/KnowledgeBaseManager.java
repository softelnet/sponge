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

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge base. Using knowledge base manager: enabling/disabling processors. Note that auto-enable is turned off in the
 * configuration.
 */
public class KnowledgeBaseManager extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("verifyTriggerEnabled", new AtomicBoolean(false));
        getSponge().setVariable("verifyTriggerDisabled", new AtomicBoolean(false));
        getSponge().setVariable("verificationDone", new AtomicBoolean(false));
    }

    public static class VerifyTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("verify");
        }

        @Override
        public void onRun(Event event) {
            verifyManager();
        }

        protected void verifyManager() {
            int triggerCount = getSponge().getEngine().getTriggers().size();
            getSponge().enableJava(TriggerA.class);
            getSponge().enableJava(TriggerA.class);
            getSponge().getVariable(AtomicBoolean.class, "verifyTriggerEnabled")
                    .set(getSponge().getEngine().getTriggers().size() == triggerCount + 1);
            triggerCount = getSponge().getEngine().getTriggers().size();
            getSponge().disableJava(TriggerA.class);
            getSponge().disableJava(TriggerA.class);
            getSponge().getVariable(AtomicBoolean.class, "verifyTriggerDisabled")
                    .set(getSponge().getEngine().getTriggers().size() == triggerCount - 1);
            getSponge().getVariable(AtomicBoolean.class, "verificationDone").set(true);
        }
    }

    public static class TriggerA extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("a");
        }

        @Override
        public void onRun(Event event) {
        }
    }

    @Override
    public void onLoad() {
        getSponge().enable(VerifyTrigger.class);
    }

    @Override
    public void onStartup() {
        getSponge().event("verify").send();
    }
}
