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

import java.util.List;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventCronEntry;
import org.openksavi.sponge.util.Manageable;

/**
 * Cron interface.
 */
public interface CronEventGenerator extends EventGenerator, Manageable {

    /**
     * Schedules an event in Cron.
     *
     * @param event
     *            event.
     * @param crontabSpec
     *            crontab time specification.
     * @return cron entry.
     */
    EventCronEntry schedule(Event event, String crontabSpec);

    /**
     * Removes a crontab entry.
     *
     * @param entry
     *            cron entry.
     * @return {@code true} if the specified entry has been scheduled.
     */
    boolean remove(EventCronEntry entry);

    /**
     * Returns all scheduled crontab entries.
     *
     * @return all scheduled crontab entries.
     */
    List<EventCronEntry> getEntries();
}
