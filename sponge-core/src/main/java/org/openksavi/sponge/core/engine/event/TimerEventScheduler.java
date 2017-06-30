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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;

import org.openksavi.sponge.core.event.GenericEventSchedulerEntry;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.engine.event.EventSchedulerTask;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * Event scheduler that uses Java Timer internally.
 */
public class TimerEventScheduler extends BaseEventGenerator implements EventScheduler {

    /** Timer. */
    private Timer timer;

    /** Scheduled task list. */
    protected Map<Object, EventSchedulerTask> scheduledEvents = new LinkedHashMap<>();

    /**
     * Creates a new event scheduler.
     *
     * @param engine the engine.
     * @param outQueue output event queue.
     */
    public TimerEventScheduler(Engine engine, EventQueue outQueue) {
        super("EventScheduler", engine, outQueue);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void startup() {
        if (isRunning()) {
            return;
        }

        timer = new Timer("EventSchedulerTimer", false);
        setRunning(true);
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        if (!isRunning()) {
            return;
        }

        setRunning(false);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Schedules an event after a specified time.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @return scheduled event task.
     */
    @Override
    public EventSchedulerEntry scheduleAfter(Event event, long delay) {
        GenericEventSchedulerEntry entry = createNewEventSchedulerEntry();
        entry.setEventName(event.getName());
        entry.setDelay(delay);

        TimerEventSchedulerTask task = new TimerEventSchedulerTask(entry, this, event);
        timer.schedule(task, delay);
        synchronized (scheduledEvents) {
            scheduledEvents.put(entry.getId(), task);
        }

        return entry;
    }

    /**
     * Schedules an event after a specified time with the specified interval.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event task.
     */
    @Override
    public EventSchedulerEntry scheduleAfter(Event event, long delay, long interval) {
        GenericEventSchedulerEntry entry = createNewEventSchedulerEntry();
        entry.setEventName(event.getName());
        entry.setDelay(delay);
        entry.setInterval(interval);

        TimerEventSchedulerTask task;
        if (interval > 0) {
            task = new TimerEventSchedulerTask(entry, this, event, true);
            timer.schedule(task, delay, interval);
        } else {
            task = new TimerEventSchedulerTask(entry, this, event);
            timer.schedule(task, delay);
        }

        synchronized (scheduledEvents) {
            scheduledEvents.put(entry.getId(), task);
        }

        return entry;
    }

    protected GenericEventSchedulerEntry createNewEventSchedulerEntry() {
        return new GenericEventSchedulerEntry(getNextEntryId());
    }

    /**
     * Schedules an event at a specified time.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @return scheduled event task.
     */
    @Override
    public EventSchedulerEntry scheduleAt(Event event, long at) {
        GenericEventSchedulerEntry entry = createNewEventSchedulerEntry();
        entry.setEventName(event.getName());
        entry.setMilliseconds(at);

        TimerEventSchedulerTask task = new TimerEventSchedulerTask(entry, this, event);
        timer.schedule(task, new Date(at));
        synchronized (scheduledEvents) {
            scheduledEvents.put(entry.getId(), task);
        }

        return entry;
    }

    /**
     * Schedules an event at a specified time with the specified interval.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event task.
     */
    @Override
    public EventSchedulerEntry scheduleAt(Event event, long at, long interval) {
        Date date = new Date(at);
        GenericEventSchedulerEntry entry = createNewEventSchedulerEntry();
        entry.setEventName(event.getName());
        entry.setMilliseconds(at);
        entry.setInterval(interval);

        TimerEventSchedulerTask task;
        if (interval > 0) {
            task = new TimerEventSchedulerTask(entry, this, event, true);
            timer.schedule(task, date, interval);
        } else {
            task = new TimerEventSchedulerTask(entry, this, event);
            timer.schedule(task, date);
        }

        synchronized (scheduledEvents) {
            scheduledEvents.put(entry.getId(), task);
        }

        return entry;
    }

    /**
     * Schedules a specified event now (inserts to the queue immediately).
     *
     * @param event event.
     */
    @Override
    public void scheduleNow(Event event) {
        putEvent(event, false);
    }

    /**
     * Returns scheduled event tasks.
     *
     * @return scheduled event tasks.
     */
    @Override
    public Collection<EventSchedulerTask> getScheduledEventTasks() {
        return scheduledEvents.values();
    }

    /**
     * Removes the specified scheduler entry.
     *
     * @param entry event scheduler entry.
     * @return {@code true} if the specified event scheduler entry has been scheduled.
     */
    @Override
    public boolean remove(EventSchedulerEntry entry) {
        EventSchedulerTask removed = null;
        synchronized (scheduledEvents) {
            removed = scheduledEvents.remove(entry.getId());
        }

        cancelTask(removed);

        return removed != null;
    }

    private void cancelTask(EventSchedulerTask task) {
        if (task instanceof TimerEventSchedulerTask) {
            ((TimerEventSchedulerTask) task).cancel();
        }
    }
}
