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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JCorrelatorBuilder;
import org.openksavi.sponge.java.JKnowledgeBase;

/**
 * Sponge Knowledge Base. Correlator builders.
 */
public class CorrelatorsBuilder extends JKnowledgeBase {

    private Map<String, List<Event>> eventLogs = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
        getSponge().setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0));
    }

    @Override
    public void onLoad() {
        getSponge().enable(new JCorrelatorBuilder("SampleCorrelator").withEvents("filesystemFailure", "diskFailure").withMaxInstances(1)
                .withOnAcceptAsFirst((correlator, event) -> event.getName().equals("filesystemFailure"))
                .withOnInit(correlator -> eventLogs.put(correlator.getMeta().getName(), new ArrayList<>()))
                .withOnEvent((correlator, event) -> {
                    List<Event> eventLog = eventLogs.get(correlator.getMeta().getName());

                    eventLog.add(event);
                    getLogger().debug("{} - event: {}, log: {}", hashCode(), event.getName(), eventLog);
                    getSponge().getVariable(AtomicInteger.class, "hardwareFailureScriptCount").incrementAndGet();
                    if (eventLog.size() == 4) {
                        getSponge().getVariable(AtomicInteger.class, "hardwareFailureScriptFinishCount").incrementAndGet();
                        correlator.finish();
                    }
                }));
    }

    @Override
    public void onStartup() {
        getSponge().event("filesystemFailure").set("source", "server1").send();
        getSponge().event("diskFailure").set("source", "server1").send();
        getSponge().event("diskFailure").set("source", "server2").send();
        getSponge().event("diskFailure").set("source", "server1").send();
        getSponge().event("diskFailure").set("source", "server2").send();
    }
}
