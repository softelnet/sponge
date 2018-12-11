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

package org.openksavi.sponge.core.engine.event;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.core.engine.EngineConstants;
import org.openksavi.sponge.core.event.AtomicLongEventIdGenerator;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventIdGenerator;

/**
 * A base event scheduler.
 */
public abstract class BaseEventScheduler extends BaseEngineModule implements EventScheduler {

    /** Current event scheduler entry id. */
    protected AtomicLong currentEntryId = new AtomicLong(1);

    /** Out event queue to which events will be inserted. */
    protected EventQueue outQueue;

    /** Event ID generator. */
    protected EventIdGenerator eventIdGenerator = new AtomicLongEventIdGenerator();

    /**
     * Creates a new event scheduler.
     *
     * @param engine the engine.
     * @param outQueue an output event queue.
     */
    protected BaseEventScheduler(SpongeEngine engine, EventQueue outQueue) {
        super("EventScheduler", engine);

        this.outQueue = outQueue;
    }

    /**
     * Schedules a specified event now (inserts to the queue immediately).
     *
     * @param event event.
     */
    @Override
    public void scheduleNow(Event event) {
        validateEvent(event);

        Validate.isTrue(event.getId() == null && event.getTime() == null, "The event with id %s has already been sent", event.getId());

        event.setId(eventIdGenerator.getNext());
        event.setTime(Instant.now());

        outQueue.put(event);
    }

    protected String getNextEntryId() {
        return getName() + "-" + currentEntryId.getAndIncrement();
    }

    public EventIdGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    @Override
    public void setEventIdGenerator(EventIdGenerator eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    protected void validateEvent(Event event) {
        Validate.notNull(event, "Event must not be null");
        Validate.isTrue(event.getName() != null && !event.getName().trim().isEmpty(), "Event name must not be null or empty");

        Validate.isTrue(
                !StringUtils.containsWhitespace(event.getName())
                        && !StringUtils.containsAny(event.getName(), EngineConstants.EVENT_NAME_RESERVED_CHARS),
                "Event name must not contain whitespaces or reserved characters %s", EngineConstants.EVENT_NAME_RESERVED_CHARS);
    }
}
