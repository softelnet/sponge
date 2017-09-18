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

package org.openksavi.sponge.test.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.event.Event;

/**
 * Correlated events (used in Rules) log used for example for test assertions.
 */
public class CorrelationEventsLog {

    public static final String VARIABLE_NAME = "correlationEventsLog";

    public static final String LABEL_ATTRIBUTE_NAME = "label";

    private Map<String, Map<String, List<List<Event>>>> events = Collections.synchronizedMap(new LinkedHashMap<>());

    private Lock lock = new ReentrantLock(true);

    public CorrelationEventsLog(String... keys) {
        init(keys);
    }

    public CorrelationEventsLog() {
        //
    }

    public void init(String... keys) {
        for (String key : keys) {
            events.put(key, createMap());
        }
    }

    private Map<String, List<List<Event>>> createMap() {
        return Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public void addEvents(String key, BaseRule rule) {
        lock.lock();
        try {
            List<Event> eventSequence = rule.getEventSequence();

            Event firstEvent = eventSequence.get(0);
            String firstLabel = firstEvent.get(LABEL_ATTRIBUTE_NAME, String.class);
            assert firstLabel != null : firstEvent;
            if (!events.containsKey(key)) {
                events.put(key, createMap());
            }

            if (!events.get(key).containsKey(firstLabel)) {
                events.get(key).put(firstLabel, new ArrayList<>());
            }

            events.get(key).get(firstLabel).add(new ArrayList<>(eventSequence));
        } finally {
            lock.unlock();
        }
    }

    public List<List<Event>> getEvents(String key, String firstEventLabel) {
        lock.lock();
        try {
            if (firstEventLabel == null) {
                return getAllEvents(key);
            }

            if (!events.containsKey(key) || !events.get(key).containsKey(firstEventLabel)) {
                return Collections.emptyList();
            }

            return events.get(key).get(firstEventLabel);
        } finally {
            lock.unlock();
        }
    }

    public List<List<Event>> getAllEvents(String key) {
        lock.lock();
        try {
            if (!events.containsKey(key)) {
                return Collections.emptyList();
            }

            return events.get(key).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        events.clear();
    }
}
