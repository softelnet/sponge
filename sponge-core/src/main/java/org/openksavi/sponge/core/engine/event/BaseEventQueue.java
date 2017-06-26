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

import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Base event queue implementation.
 */
public abstract class BaseEventQueue extends BaseEngineModule implements EventQueue {

    /** Queue capacity. -1 means unlimited. */
    protected int capacity = -1;

    /**
     * Creates a new event queue.
     *
     * @param name
     *            queue name.
     */
    protected BaseEventQueue(String name) {
        super(name);
    }

    /**
     * Sets the capacity of the queue.
     *
     * @param capacity
     *            the capacity of the queue.
     */
    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns the capacity of the queue.
     *
     * @return the capacity of the queue.
     */
    @Override
    public int getCapacity() {
        return capacity;
    }
}
