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

package org.openksavi.sponge.engine.event;

import java.util.List;

import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventIdGenerator;
import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * Event scheduler.
 */
public interface EventScheduler extends EngineModule {

    /**
     * Puts a specified event into the event queue now.
     *
     * @param event event.
     */
    void scheduleNow(Event event);

    /**
     * Schedules an event after a specified time.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAfter(Event event, long delay);

    /**
     * Schedules an event after a specified time with the specified interval.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAfter(Event event, long delay, long interval);

    /**
     * Schedules an event at a specified time.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAt(Event event, long at);

    /**
     * Schedules an event at a specified time with the specified interval.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry scheduleAt(Event event, long at, long interval);

    /**
     * Schedules an event in Cron.
     *
     * @param event event.
     * @param crontabSpec crontab time specification.
     * @return cron entry.
     */
    EventSchedulerEntry scheduleAt(Event event, String crontabSpec);

    /**
     * Removes the specified event scheduler entry.
     *
     * @param entry event scheduler entry.
     * @return {@code true} if the specified event scheduler entry has been scheduled.
     */
    boolean remove(EventSchedulerEntry entry);

    /**
     * Returns all scheduled entries.
     *
     * @return all scheduled entries.
     */
    List<EventSchedulerEntry> getEntries();

    /**
     * Sets an event ID generator.
     *
     * @param eventIdGenerator an event ID generator.
     */
    void setEventIdGenerator(EventIdGenerator eventIdGenerator);
}
