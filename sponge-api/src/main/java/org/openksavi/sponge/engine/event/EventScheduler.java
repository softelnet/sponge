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

package org.openksavi.sponge.engine.event;

import java.util.Collection;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * Event scheduler.
 */
public interface EventScheduler extends EventGenerator {

    /**
     * Schedules an event after a specified time.
     *
     * @param event
     *            event.
     * @param delay
     *            delay in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAfter(Event event, long delay);

    /**
     * Schedules an event after a specified time with the specified interval.
     *
     * @param event
     *            event.
     * @param delay
     *            delay in milliseconds.
     * @param interval
     *            interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAfter(Event event, long delay, long interval);

    /**
     * Schedules an event at a specified time.
     *
     * @param event
     *            event.
     * @param at
     *            time in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAt(Event event, long at);

    /**
     * Schedules an event at a specified time with the specified interval.
     *
     * @param event
     *            event.
     * @param at
     *            time in milliseconds.
     * @param interval
     *            interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAt(Event event, long at, long interval);

    /**
     * Schedules a specified event now (inserts to the queue immediately).
     *
     * @param event
     *            event.
     */
    void scheduleNow(Event event);

    /**
     * Returns scheduled event tasks.
     *
     * @return scheduled event tasks.
     */
    Collection<EventSchedulerTask> getScheduledEventTasks();

    /**
     * Removes the specified event task.
     *
     * @param entry
     *            event scheduler entry.
     * @return {@code true} if the specified event scheduler entry has been scheduled.
     */
    boolean remove(EventSchedulerEntry entry);
}
