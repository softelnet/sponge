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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.ShapeFilter;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge base. Using java filters.
 */
public class FiltersJava extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        Map<String, AtomicInteger> eventCounter = Collections.synchronizedMap(new HashMap<>());
        eventCounter.put("e1", new AtomicInteger(0));
        eventCounter.put("e2", new AtomicInteger(0));
        eventCounter.put("e3", new AtomicInteger(0));
        getSponge().setVariable("eventCounter", eventCounter);
    }

    public static class FilterTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2", "e3");
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onRun(Event event) {
            getLogger().debug("Processing trigger for event {}", event);
            ((Map<String, AtomicInteger>) getSponge().getVariable("eventCounter")).get(event.getName()).incrementAndGet();
        }
    }

    @Override
    public void onLoad() {
        getSponge().enableJava(ShapeFilter.class);
    }

    @Override
    public void onStartup() {
        getSponge().event("e1").sendAfter(100, 100);
        getSponge().event("e2").set("shape", "square").sendAfter(200, 100);
        getSponge().event("e3").set("shape", "circle").sendAfter(300, 100);
    }
}
