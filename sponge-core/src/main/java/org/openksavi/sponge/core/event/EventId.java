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

package org.openksavi.sponge.core.event;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Event ID.
 */
public class EventId implements Serializable, Comparable<EventId> {

    private static final long serialVersionUID = -2182861627248524552L;

    private static final String SEPARATOR = "-";

    private long baseTimestamp;

    private long id;

    public EventId(long baseTimestamp, long id) {
        this.baseTimestamp = baseTimestamp;
        this.id = id;
    }

    public long getBaseTimestamp() {
        return baseTimestamp;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return baseTimestamp + SEPARATOR + id;
    }

    public static EventId fromString(String s) {
        String[] elements = StringUtils.split(s, SEPARATOR);

        return new EventId(Long.parseLong(elements[0]), Long.parseLong(elements[1]));
    }

    @Override
    public int compareTo(EventId event) {
        return new CompareToBuilder().append(baseTimestamp, event.baseTimestamp).append(id, event.id).toComparison();
    }

    public static int compare(String id1, String id2) {
        return EventId.fromString(id1).compareTo(EventId.fromString(id2));
    }
}
