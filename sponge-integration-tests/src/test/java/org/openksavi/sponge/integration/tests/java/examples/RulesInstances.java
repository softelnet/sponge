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
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;

/**
 * Sponge Knowledge Base. Rules - instances.
 */
public class RulesInstances extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("countA", new AtomicInteger(0));
        getSponge().setVariable("countB", new AtomicInteger(0));
        getSponge().setVariable("max", 100);
    }

    public static class RuleA extends JRule {

        @Override
        public void onConfigure() {
            withEvents("a a1", "a a2");
        }

        @Override
        public void onRun(Event event) {
            getSponge().getVariable(AtomicInteger.class, "countA").incrementAndGet();
        }
    }

    public static class RuleB extends JRule {

        @Override
        public void onConfigure() {
            withEvents("b b1", "b b2");
        }

        @Override
        public void onRun(Event event) {
            getSponge().getVariable(AtomicInteger.class, "countB").incrementAndGet();
        }
    }

    @Override
    public void onStartup() {
        for (int i = 0; i < getSponge().getVariable(Number.class, "max").intValue(); i++) {
            getSponge().event("a").send();
            getSponge().event("b").send();
        }
    }
}
