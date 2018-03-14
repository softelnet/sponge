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

package org.openksavi.sponge.core.engine;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.openksavi.sponge.engine.EventQueueManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Event Queue Manager.
 */
public class DefaultEventQueueManager extends BaseEngineModule implements EventQueueManager {

    /** An Input Event Queue. */
    private EventQueue inputEventQueue;

    /** A Main Event Queue. */
    private EventQueue mainEventQueue;

    /** An Output Event Queue. */
    private EventQueue outputEventQueue;

    /**
     * Creates a new Event Queue Manager.
     *
     * @param engine the engine.
     */
    public DefaultEventQueueManager(SpongeEngine engine) {
        super("EventQueueManager", engine);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        getEventQueues().forEach(queue -> queue.setEngine(getEngine()));

        outputEventQueue.startup();
        mainEventQueue.startup();
        inputEventQueue.startup();
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void doShutdown() {
        inputEventQueue.shutdown();
        mainEventQueue.shutdown();
        outputEventQueue.shutdown();
    }

    @Override
    public EventQueue getInputEventQueue() {
        return inputEventQueue;
    }

    @Override
    public void setInputEventQueue(EventQueue inputEventQueue) {
        this.inputEventQueue = inputEventQueue;
    }

    @Override
    public EventQueue getMainEventQueue() {
        return mainEventQueue;
    }

    @Override
    public void setMainEventQueue(EventQueue mainEventQueue) {
        this.mainEventQueue = mainEventQueue;
    }

    @Override
    public EventQueue getOutputEventQueue() {
        return outputEventQueue;
    }

    @Override
    public void setOutputEventQueue(EventQueue outputEventQueue) {
        this.outputEventQueue = outputEventQueue;
    }

    /**
     * Returns event queues.
     *
     * @return event queues.
     */
    @Override
    public List<EventQueue> getEventQueues() {
        return ImmutableList.of(inputEventQueue, mainEventQueue, outputEventQueue);
    }
}
