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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.core.library.Deduplication;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.SameSourceJavaUnorderedRule;
import org.openksavi.sponge.java.JFilter;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.java.JTrigger;
import org.openksavi.sponge.rule.Rule;

/**
 * Sponge Knowledge Base. Using unordered rules.
 */
public class UnorderedRules extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
        getSponge().setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
        getSponge().setVariable("sameSourceFirstFireCount", new AtomicInteger(0));
    }

    public static class FirstRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("filesystemFailure", "diskFailure").withOrdered(false);
            withAllConditions((Rule rule, Event event) -> rule.getFirstEvent().get("source").equals(event.get("source")),
                    (Rule rule, Event event) -> Duration.between(rule.getFirstEvent().getTime(), event.getTime()).getSeconds() <= 2);
            withDuration(Duration.ofSeconds(5));
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Running rule for events: {}", getEventSequence());
            getSponge().getVariable(AtomicInteger.class, "sameSourceFirstFireCount").incrementAndGet();
            getSponge().event("alarm").set("source", getFirstEvent().get("source")).send();
        }
    }

    public static class SameSourceAllRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("filesystemFailure e1", "diskFailure e2 :all").withOrdered(false);
            withCondition("e1", "severityCondition");
            withConditions("e2", "severityCondition", "diskFailureSourceCondition");
            withDuration(Duration.ofSeconds(5));
        }

        @Override
        public void onRun(Event event) {
            getLogger().info("Monitoring log [{}]: Critical failure in {}! Events: {}", event != null ? event.getTime() : null,
                    event != null ? event.get("source") : null, getEventSequence());
            getSponge().getVariable(AtomicInteger.class, "hardwareFailureScriptCount").incrementAndGet();
        }

        public boolean severityCondition(Event event) {
            return event.get(Number.class, "severity").intValue() > 5;
        }

        public boolean diskFailureSourceCondition(Event event) {
            // Both events have to have the same source
            return Objects.equals(event.get("source"), getFirstEvent().get("source"))
                    && Duration.between(getFirstEvent().getTime(), event.getTime()).getSeconds() <= 4;
        }
    }

    public static class AlarmFilter extends JFilter {

        private Deduplication deduplication = new Deduplication("source");

        @Override
        public void onConfigure() {
            withEvent("alarm");
        }

        @Override
        public void onInit() {
            deduplication.getCacheBuilder().expireAfterWrite(2, TimeUnit.SECONDS);
        }

        @Override
        public boolean onAccept(Event event) {
            return deduplication.onAccept(event);
        }
    }

    public static class Alarm extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("alarm");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received alarm from {}", event.get(String.class, "source"));
        }
    }

    @Override
    public void onLoad() {
        getSponge().enableJava(SameSourceJavaUnorderedRule.class);
    }

    @Override
    public void onStartup() {
        getSponge().event("diskFailure").set("severity", 10).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 10).set("source", "server2").send();
        getSponge().event("diskFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("filesystemFailure").set("severity", 8).set("source", "server1").send();
        getSponge().event("filesystemFailure").set("severity", 6).set("source", "server1").send();
        getSponge().event("diskFailure").set("severity", 6).set("source", "server1").send();
    }
}
