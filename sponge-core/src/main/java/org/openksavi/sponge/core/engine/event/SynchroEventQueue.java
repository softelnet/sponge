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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.event.Event;

/**
 * A queue that blocks put operation when a queue is full.
 */
public class SynchroEventQueue extends BaseEventQueue {

    /** Thread safe priority blocking queue. */
    private LinkedBlockingQueue<Event> queue;

    /** Synchronization lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new priority event queue.
     *
     * @param name queue name.
     */
    public SynchroEventQueue(String name) {
        super(name);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        queue = capacity >= 0 ? new LinkedBlockingQueue<>(getCapacity()) : new LinkedBlockingQueue<>();
    }

    /**
     * Puts a new event into the event queue. Blocks if there is no capacity.
     *
     * @param event a new event.
     * @throws QueueFullException when the queue is full.
     */
    @Override
    public void put(Event event) {
        lock.lock();

        try {
            boolean success = false;

            while (!success) {
                try {
                    queue.add(event);
                    success = true;
                } catch (IllegalStateException e) {
                    // If the queue is full, than try again after sleep.
                    try {
                        TimeUnit.MILLISECONDS.sleep(getEngine().getDefaultParameters().getInternalQueueBlockingPutSleep());
                    } catch (InterruptedException ie) {
                        throw new SpongeException(ie);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the first event from the queue.
     *
     * @return the first event from the queue.
     */
    @Override
    public Event get(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the current size of the queue.
     *
     * @return the current size of the queue.
     */
    @Override
    public int getSize() {
        return queue.size();
    }

    /**
     * Clears this event queue.
     */
    @Override
    public void clear() {
        queue.clear();
    }
}
