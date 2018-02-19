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
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JRule;

public class SameSourceJavaUnorderedRule extends JRule {

    private static final Logger logger = LoggerFactory.getLogger(SameSourceJavaUnorderedRule.class);

    @Override
    public void onConfigure() {
        setEvents("filesystemFailure e1", "diskFailure e2 :all");
        setOrdered(false);

        addAllConditions("severityCondition");
        addEventConditions("e2", (rule, event) -> {
            // Both events have to have the same source
            return event.get("source").equals(rule.getFirstEvent().get("source"))
                    && Duration.between(rule.getFirstEvent().getTime(), event.getTime()).getSeconds() <= 4;
        });

        setDuration(Duration.ofSeconds(5));
    }

    @Override
    public void onRun(Event event) {
        logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.getTime(), event.get("source"), getEventSequence());
        getEps().getVariable(AtomicInteger.class, "hardwareFailureJavaCount").incrementAndGet();
    }

    public boolean severityCondition(Event event) {
        return event.get("severity", Number.class).intValue() > 5;
    }
}
