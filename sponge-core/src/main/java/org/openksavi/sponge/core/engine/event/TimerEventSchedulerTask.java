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

import java.util.TimerTask;

import org.openksavi.sponge.engine.event.EventSchedulerTask;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * Event scheduler task.
 */
public class TimerEventSchedulerTask extends TimerTask implements EventSchedulerTask {

    private static final long serialVersionUID = 7515572901856671501L;

    /** Scheduled event. */
    private Event event;

    /** Whether this task defines sequential events or not. */
    private boolean sequential = false;

    /** Generated event counter. */
    private long counter = 0;

    /** Event scheduler reference. */
    private transient TimerEventScheduler eventScheduler;

    private EventSchedulerEntry entry;

    /**
     * Creates a new event scheduler task.
     *
     * @param entry an event scheduler entry.
     * @param eventScheduler an event scheduler reference.
     * @param event the scheduled event.
     * @param sequential whether this task defines sequential events or not.
     */
    public TimerEventSchedulerTask(EventSchedulerEntry entry, TimerEventScheduler eventScheduler, Event event, boolean sequential) {
        this.entry = entry;
        this.eventScheduler = eventScheduler;
        this.event = event;
        this.sequential = sequential;
    }

    /**
     * Creates a new event scheduler task.
     *
     * @param entry an event scheduler entry.
     * @param eventScheduler an event scheduler reference.
     * @param event the scheduled event.
     */
    public TimerEventSchedulerTask(EventSchedulerEntry entry, TimerEventScheduler eventScheduler, Event event) {
        this(entry, eventScheduler, event, false);
    }

    /**
     * Runs this event scheduler task.
     */
    @Override
    public void run() {
        try {
            eventScheduler.putEvent(event, (sequential && counter >= 0));
            counter++;

            if (!sequential) {
                eventScheduler.remove(entry);
            }
        } catch (Throwable e) {
            eventScheduler.getEngine().handleError(eventScheduler.getName(), e);
        }
    }

    /**
     * Returns the scheduled event.
     *
     * @return an event.
     */
    @Override
    public Event getEvent() {
        return event;
    }

    /**
     * Returns generated event counter.
     *
     * @return generated event counter.
     */
    @Override
    public long getCounter() {
        return counter;
    }

    /**
     * Returns string representation.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        return event + (sequential ? (", counter: " + counter) : "");
    }
}
