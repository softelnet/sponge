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

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.test.util.CorrelationEventsLog;

/**
 * Sponge Knowledge base. Using rules - events.
 */
public class RulesNoneModeEventsConditions extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("correlationEventsLog", new CorrelationEventsLog());
    }

    // Naming F(irst), L(ast), A(ll), N(one)

    public static class RuleFNF extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2 :none", "e3").withCondition("e2", "e2LabelCondition");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running rule for events: {}", getEventAliasMap());
            getSponge().getVariable(CorrelationEventsLog.class, "correlationEventsLog").addEvents(getMeta().getName(), this);
        }

        public boolean e2LabelCondition(Event event) {
            return Integer.parseInt(event.get(String.class, "label")) > 4;
        }
    }

    public static class RuleFNNFReject extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2 :none", "e6 :none", "e3").withCondition("e2", "e2LabelCondition");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running rule for events: {}", getEventAliasMap());
            getSponge().getVariable(CorrelationEventsLog.class, "correlationEventsLog").addEvents(getMeta().getName(), this);
        }

        public boolean e2LabelCondition(Event event) {
            int label = Integer.parseInt(event.get(String.class, "label"));
            return label >= 2 && label <= 4;
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("e1").set("label", "1").send();
        getSponge().event("e2").set("label", "2").send();
        getSponge().event("e2").set("label", "3").send();
        getSponge().event("e2").set("label", "4").send();
        getSponge().event("e3").set("label", "5").send();
        getSponge().event("e3").set("label", "6").send();
        getSponge().event("e3").set("label", "7").send();
    }
}
