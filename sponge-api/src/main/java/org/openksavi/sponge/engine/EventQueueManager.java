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

package org.openksavi.sponge.engine;

import java.util.List;

import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Event Queue Manager.
 */
public interface EventQueueManager extends EngineModule {

    /**
     * Returns an Input Event Queue.
     *
     * @return an Input Event Queue.
     */
    EventQueue getInputEventQueue();

    /**
     * Sets an Input Event Queue.
     *
     * @param inputEventQueue an Input Event Queue.
     */
    void setInputEventQueue(EventQueue inputEventQueue);

    /**
     * Returns a Main Event Queue.
     *
     * @return a Main Event Queue.
     */
    EventQueue getMainEventQueue();

    /**
     * Sets a Main Event Queue.
     *
     * @param mainEventQueue a Main Event Queue.
     */
    void setMainEventQueue(EventQueue mainEventQueue);

    /**
     * Returns an Output Event Queue.
     *
     * @return an Output Event Queue.
     */
    EventQueue getOutputEventQueue();

    /**
     * Sets an Output Event Queue.
     *
     * @param outputEventQueue an Output Event Queue.
     */
    void setOutputEventQueue(EventQueue outputEventQueue);

    /**
     * Returns event queues.
     *
     * @return event queues.
     */
    List<EventQueue> getEventQueues();
}
