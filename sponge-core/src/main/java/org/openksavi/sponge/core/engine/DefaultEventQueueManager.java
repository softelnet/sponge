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

package org.openksavi.sponge.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.EventQueueManager;
import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Event Queue Manager.
 */
public class DefaultEventQueueManager extends BaseEngineModule implements EventQueueManager {

    /** The list of event queues. */
    private List<EventQueue> eventQueues = Collections.synchronizedList(new ArrayList<>());

    /**
     * Creates a new Event Queue Manager.
     *
     * @param engine
     *            the engine.
     */
    public DefaultEventQueueManager(Engine engine) {
        super("EventQueueManager", engine);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void startup() {
        if (isRunning()) {
            return;
        }

        eventQueues.forEach(queue -> queue.startup());

        setRunning(true);
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        setRunning(false);

        eventQueues.forEach(queue -> queue.shutdown());
    }

    /**
     * Returns event queues.
     *
     * @return event queues.
     */
    @Override
    public List<EventQueue> getEventQueues() {
        return eventQueues;
    }

    /**
     * Adds a new event queue.
     *
     * @param eventQueue
     *            event queue.
     */
    @Override
    public void addEventQueue(EventQueue eventQueue) {
        eventQueue.setEngine(engine);

        eventQueues.add(eventQueue);
    }
}
