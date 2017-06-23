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

import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.event.Event;

/**
 * Null event queue that forgets all events.
 * It can be used as event queue terminator.
 */
public class NullEventQueue extends BaseEventQueue {

    /**
     * Creates a new null event queue.
     */
    public NullEventQueue() {
        super("NullEventQueue");
    }

    /**
     * Empty implementation.
     */
    @Override
    public void put(Event event) throws QueueFullException {
        //
    }

    /**
     * Always returns {@code null}.
     */
    @Override
    public Event get(long timeout) throws InterruptedException {
        return null;
    }

    /**
     * Empty implementation.
     */
    @Override
    public void setMaxSize(int maxSize) {
    }

    /**
     * Always returns {@code -1}, that means unlimited size.
     */
    @Override
    public int getMaxSize() {
        return -1;
    }

    /**
     * Clears this event queue.
     */
    @Override
    public void clear() {
    }
}
