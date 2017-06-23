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

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.event.EventGenerator;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.Event;

/**
 * Base event generator.
 */
public abstract class BaseEventGenerator extends BaseEngineModule implements EventGenerator {

    /** Out event queue to which events will be inserted. */
    protected EventQueue outQueue;

    protected AtomicLong currentEntryId = new AtomicLong(1);

    /**
     * Creates a new event scheduler.
     *
     * @param name
     *            name.
     * @param engine
     *            the engine.
     * @param outQueue
     *            output event queue.
     */
    public BaseEventGenerator(String name, Engine engine, EventQueue outQueue) {
        super(name, engine);
        this.outQueue = outQueue;
    }

    protected String getNextEntryId() {
        return getName() + "-" + currentEntryId.getAndIncrement();
    }

    @Override
    public void putEvent(Event event, boolean newInstance) {
        if (newInstance) {
            event = event.clone();
        }

        event.setId(engine.newGlobalEventId());
        event.setTime(Instant.now());

        outQueue.put(event);
    }
}
