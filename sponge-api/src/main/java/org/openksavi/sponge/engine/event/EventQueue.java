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

import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.event.Event;

/**
 * Event queue.
 * Event queue may have a limited size.
 */
public interface EventQueue extends EngineModule {

    /**
     * Puts a new event into the event queue.
     *
     * @param event
     *            a new event.
     * @throws org.openksavi.sponge.engine.QueueFullException
     *             when the queue is full.
     */
    void put(Event event) throws QueueFullException;

    /**
     * Returns the first event from the queue.
     *
     * @param timeout
     *            the timeout in milliseconds.
     *
     * @return the first event from the queue or {@code null} when there was none.
     * @throws java.lang.InterruptedException
     *             if any.
     */
    Event get(long timeout) throws InterruptedException;

    /**
     * Sets the maximum size of the queue.
     *
     * @param maxSize
     *            the maximum size of the queue.
     */
    void setMaxSize(int maxSize);

    /**
     * Returns the maximum size of the queue.
     *
     * @return the maximum size of the queue.
     */
    int getMaxSize();

    /**
     * Returns the name of the event queue.
     *
     * @return the name of the event queue.
     */
    @Override
    String getName();

    /**
     * Clears this event queue.
     */
    void clear();
}
