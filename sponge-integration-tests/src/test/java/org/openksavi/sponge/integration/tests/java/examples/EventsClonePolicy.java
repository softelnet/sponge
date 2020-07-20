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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinitionModifier;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Deep and shallow event clone policy.
 */
public class EventsClonePolicy extends JKnowledgeBase {

    private Map<String, List<Event>> events = Collections.synchronizedMap(new HashMap<>());

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void onInit() {
        // Variables for assertions only
        events.put("defaultClonePolicy", new ArrayList());
        events.put("deepClonePolicy", new ArrayList());
        events.put("shallowClonePolicy", new ArrayList());
        getSponge().setVariable("events", events);
    }

    public static class ClonePolicyTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvents("defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy");
        }

        @Override
        public void onRun(Event event) {
            Map<String, List<Event>> events = getSponge().getVariable("events");

            if (events.get(event.getName()) != null) {
                events.get(event.getName()).add(event);
                getLogger().debug("Processing event: {}", event.getName());
                Map<String, Object> map = event.get("map");
                getLogger().debug("map attribute (before): {}", map);
                map.put("a", "Value " + events.get(event.getName()).size());
                getLogger().debug("map attribute (after): {}", map);
            }
        }
    }

    @Override
    public void onStartup() {
        EventDefinitionModifier setEventAttributes = (Event event) -> {
            Map<String, Object> hash = new HashMap<>();
            hash.put("a", "Value 0");
            hash.put("b", Arrays.asList(java.lang.Boolean.TRUE));
            event.set("map", hash);
            event.set("integer", new Integer(10));
        };

        getSponge().event("defaultClonePolicy").modify(setEventAttributes).sendAfter(100, 1000);
        getSponge().event("deepClonePolicy", EventClonePolicy.DEEP).modify(setEventAttributes).sendAfter(200, 1000);
        getSponge().event("shallowClonePolicy", EventClonePolicy.SHALLOW).modify(setEventAttributes).sendAfter(400, 1000);
    }
}
