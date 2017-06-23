/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.event.Event;

/**
 * Events log used for example for test assertions.
 */
public class EventsLog {

    public static final String VARIABLE_NAME = "eventsLog";

    private Map<String, List<Event>> events = Collections.synchronizedMap(new HashMap<>());

    public EventsLog(String... keys) {
        init(keys);
    }

    public EventsLog() {
        //
    }

    public void init(String... keys) {
        for (String key : keys) {
            events.put(key, Collections.synchronizedList(new ArrayList<>()));
        }
    }

    public void addEvent(String key, Event event) {
        events.get(key).add(event);
    }

    public List<Event> getEvents(String key) {
        return events.get(key);
    }

    public static EventsLog getInstance(EngineOperations engineOperations) {
        return engineOperations.getVariable(EventsLog.class, VARIABLE_NAME);
    }
}
