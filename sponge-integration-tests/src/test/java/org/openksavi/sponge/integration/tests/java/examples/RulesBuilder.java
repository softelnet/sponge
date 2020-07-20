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
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRuleBuilder;

/**
 * Sponge Knowledge Base. Rule builders.
 */
public class RulesBuilder extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
        getSponge().setVariable("sameSourceFirstFireCount", new AtomicInteger(0));
    }

    @Override
    public void onLoad() {
        getSponge().enable(new JRuleBuilder("FirstRule").withEvents("filesystemFailure", "diskFailure")
                .withCondition("diskFailure",
                        (rule, event) -> Duration.between(rule.getEvent("filesystemFailure").getTime(), event.getTime()).getSeconds() >= 0)
                .withOnRun((rule, event) -> {
                    rule.getLogger().debug("Running rule for event: {}", event != null ? event.getName() : null);
                    getSponge().getVariable(AtomicInteger.class, "sameSourceFirstFireCount").incrementAndGet();
                }));

        getSponge().enable(new JRuleBuilder("SameSourceAllRule").withEvents("filesystemFailure e1", "diskFailure e2 :all")
                .withCondition("e1", (rule, event) -> event.get(Number.class, "severity").intValue() > 5)
                .withCondition("e2", (rule, event) -> event.get(Number.class, "severity").intValue() > 5)
                .withCondition("e2", (rule, event) -> {
                    // Both events have to have the same source
                    Event event1 = rule.getEvent("e1");
                    return event.get("source").equals(event1.get("source"))
                            && Duration.between(event1.getTime(), event.getTime()).getSeconds() <= 4;
                }).withDuration(Duration.ofSeconds(8)).withOnRun((rule, event) -> {
                    rule.getLogger().info("Monitoring log [{}]: Critical failure in {}! Events: {}", event != null ? event.getTime() : null,
                            event != null ? event.get("source") : null, rule.getEventSequence());
                    getSponge().getVariable(AtomicInteger.class, "hardwareFailureScriptCount").incrementAndGet();
                }));
    }

    @Override
    public void onStartup() {
        getSponge().event("filesystemFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 10).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 10).set("source", "server2").send();
        getSponge().event("diskFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 1).set("source", "server1").send();
    }
}
