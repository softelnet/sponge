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

package org.openksavi.sponge.core.engine.event;

import java.util.Comparator;

import org.openksavi.sponge.core.event.EventId;
import org.openksavi.sponge.event.Event;

/**
 * Comparator for events that are being put into a priority queue.
 */
public class PriorityEventQueueComparator implements Comparator<Event> {

    public static final Comparator<Event> INSTANCE = new PriorityEventQueueComparator();

    @Override
    public int compare(Event event1, Event event2) {
        // Priority should be greater for the more important event.
        int priorityComaprison = event2.getPriority() - event1.getPriority();

        if (priorityComaprison != 0) {
            return priorityComaprison * 2;
        }

        // ID should be lower for the more important event.
        return EventId.compare(event1.getId(), event2.getId());
    }

}
