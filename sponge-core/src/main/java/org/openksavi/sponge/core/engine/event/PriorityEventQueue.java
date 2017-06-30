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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.event.Event;

/**
 * A priority event queue. Event queue may have a limited size. It supports priority of events. Greater value of the priority signifies a
 * more important event.
 */
public class PriorityEventQueue extends BaseEventQueue {

    protected static final int QUEUE_INITIAL_CAPACITY = 100;

    /** Thread safe priority blocking queue. */
    private BlockingQueue<Event> queue;

    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new priority event queue.
     *
     * @param name queue name.
     */
    public PriorityEventQueue(String name) {
        super(name);

        queue = createBlockingQueue();
    }

    protected BlockingQueue<Event> createBlockingQueue() {
        return new PriorityBlockingQueue<>(QUEUE_INITIAL_CAPACITY, new PriorityEventQueueComparator());
    }

    /**
     * Puts a new event into the event queue.
     *
     * @param event a new event.
     * @throws QueueFullException when the queue is full.
     */
    @Override
    public void put(Event event) throws QueueFullException {
        lock.lock();

        try {
            if (capacity > -1 && queue.size() >= capacity) {
                throw new QueueFullException("Event queue " + getName() + " is full. Capacity is " + capacity + ".");
            }

            queue.add(event);
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
