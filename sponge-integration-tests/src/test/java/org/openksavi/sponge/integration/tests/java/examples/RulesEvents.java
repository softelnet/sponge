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

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.util.CorrelationEventsLog;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;

/**
 * Sponge Knowledge base. Using rules - events.
 */
public class RulesEvents extends JKnowledgeBase {

    public static long defaultDuration = 1000;

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("correlationEventsLog", new CorrelationEventsLog());
    }

    // Naming F(irst), L(ast), A(ll), N(one)

    public static class RuleF extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1");
        }

        @Override
        public void onRun(Event event) {
            getSponge().getVariable(CorrelationEventsLog.class, "correlationEventsLog").addEvents(getMeta().getName(), this);
        }
    }

    // F(irst)F(irst)F(irst)
    public static class RuleFFF extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2", "e3 :first");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running rule for event: {}", event != null ? event.getName() : null);
            getSponge().getVariable(CorrelationEventsLog.class, "correlationEventsLog").addEvents(getMeta().getName(), this);
        }
    }

    public static abstract class TestRule extends JRule {

        public void setup(String... eventSpec) {
            withEvents(eventSpec).withDuration(Duration.ofMillis(defaultDuration));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running rule for event: {}, sequence: {}", event != null ? event.getName() : null,
                    SpongeUtils.toStringEventSequence(getEventSequence(), "label"));
            getSponge().getVariable(CorrelationEventsLog.class, "correlationEventsLog").addEvents(getMeta().getName(), this);
        }
    }

    public static class RuleFFFDuration extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2", "e3 :first");
        }
    }

    // F(irst)F(irst)L(ast)
    public static class RuleFFL extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2", "e3 :last");
        }
    }

    // F(irst)F(irst)A(ll)
    public static class RuleFFA extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2", "e3 :all");
        }
    }

    // F(irst)F(irst)N(one)
    public static class RuleFFN extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2", "e4 :none");
        }
    }

    // F(irst)L(ast)F(irst)
    public static class RuleFLF extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :last", "e3 :first");
        }
    }

    // F(irst)L(ast)L(ast)
    public static class RuleFLL extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :last", "e3 :last");
        }
    }

    // F(irst)L(ast)A(ll)
    public static class RuleFLA extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :last", "e3 :all");
        }
    }

    // F(irst)L(ast)N(one)
    public static class RuleFLN extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :last", "e4 :none");
        }
    }

    // F(irst)A(ll)F(irst)
    public static class RuleFAF extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :all", "e3 :first");
        }
    }

    // F(irst)A(ll)L(ast)
    public static class RuleFAL extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :all", "e3 :last");
        }
    }

    // F(irst)A(ll)A(ll)
    public static class RuleFAA extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :all", "e3 :all");
        }
    }

    // F(irst)A(ll)N(one)
    public static class RuleFAN extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :all", "e5 :none");
        }
    }

    // F(irst)N(one)F(irst)
    public static class RuleFNF extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e5 :none", "e3");
        }
    }

    // F(irst)N(one)L(ast)
    public static class RuleFNL extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e5 :none", "e3 :last");
        }
    }

    // F(irst)N(one)A(ll)
    public static class RuleFNA extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e5 :none", "e3 :all");
        }
    }

    public static class RuleFNFReject extends TestRule {

        @Override
        public void onConfigure() {
            setup("e1", "e2 :none", "e3");
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("e1").set("label", "0").sendAfter(0, 200); // Not used in assertions, "background noise" events.
        getSponge().event("e1").set("label", "-1").sendAfter(0, 200);
        getSponge().event("e1").set("label", "-2").sendAfter(0, 200);
        getSponge().event("e1").set("label", "-3").sendAfter(0, 200);

        getSponge().event("e1").set("label", "1").send();
        getSponge().event("e2").set("label", "2").send();
        getSponge().event("e2").set("label", "3").send();
        getSponge().event("e2").set("label", "4").send();
        getSponge().event("e3").set("label", "5").send();
        getSponge().event("e3").set("label", "6").send();
        getSponge().event("e3").set("label", "7").send();
    }
}
