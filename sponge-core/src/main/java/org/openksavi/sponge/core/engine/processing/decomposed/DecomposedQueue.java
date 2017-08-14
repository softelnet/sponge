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

package org.openksavi.sponge.core.engine.processing.decomposed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.core.engine.processing.EventProcessorRegistrationListener;
import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.event.Event;

/**
 * Decomposed custom queue of entries (trigger adapter or event set processor group adapter, event).
 */
public class DecomposedQueue<T extends EventProcessorAdapter<?>> implements EventProcessorRegistrationListener<T> {

    private static final Logger logger = LoggerFactory.getLogger(DecomposedQueue.class);

    /** A list containing pairs (trigger adapter or event set processor group adapter, event). */
    private List<Pair<T, Event>> entries;

    /** Allows for concurrent processing of events that have the same type. Default is {@code true}. */
    private boolean allowConcurrentEventTypeProcessing;

    /** Currently processed non-singletons. */
    private Set<String> currentlyProcessedNonSingletons = Collections.synchronizedSet(new HashSet<>());

    /** Currently processed event types (i.e. events that have the same name). */
    private Set<String> currentlyProcessedEventNames = Collections.synchronizedSet(new HashSet<>());

    /** Queue capacity. */
    private int capacity;

    /** Main lock. */
    private Lock lock = new ReentrantLock(true);

    /** Condition for waiting if no entries are found for processing. */
    private Condition lockCondition = lock.newCondition();

    /** Internal lock for atomic operations on internal data structures. */
    private Lock internalLock = new ReentrantLock(true);

    public DecomposedQueue(int capacity, boolean allowConcurrentEventTypeProcessing) {
        this.capacity = capacity;
        this.allowConcurrentEventTypeProcessing = allowConcurrentEventTypeProcessing;

        entries = Collections.synchronizedList(new ArrayList<>(capacity));
    }

    /**
     * Puts a new entry (trigger adapter or event set processor group adapter, event) at the end of this decomposed queue.
     *
     * @param entry a pair (trigger adapter or event set processor group adapter).
     */
    public void put(Pair<T, Event> entry) {
        lock.lock();

        try {
            internalLock.lock();

            if (entries.size() >= capacity) {
                throw new QueueFullException("Decomposed queue is full");
            }

            logger.debug("Put: {}", entry);
            entries.add(entry);
            lockCondition.signal();
        } finally {
            internalLock.unlock();
            lock.unlock();
        }
    }

    /**
     * Returns the next pair (trigger adapter or event set processor group adapter) available to processing. Trigger adapter is always ready
     * to process an event because it is a singleton. Event set processor group adapter is ready to process an event only one at a time,
     * i.e. processed events sequentially to preserve event order.
     *
     * <p>This method is blocking.
     *
     * <p>When timeout occurs {@code null} is returned.
     *
     * <p>If this method returns non-{@code null} value, you have to invoke {@code release} after the processing by the returned adapter
     * completes.
     *
     * @param timeout how long to wait before giving up, in units of {@code unit}.
     * @param unit unit a {@code TimeUnit} determining how to interpret the {@code timeout} parameter.
     * @return the next pair (trigger adapter or event set processor group adapter) available to processing.
     * @throws InterruptedException if interrupted while waiting.
     */
    public Pair<T, Event> get(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();

        try {
            Pair<T, Event> entry;

            while ((entry = doGetNonBlocking()) == null) {
                // If timeout then return null.
                if (!lockCondition.await(timeout, unit)) {
                    break;
                }
            }

            if (entry != null) {
                logger.debug("Get: {}", entry);
            }

            return entry;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the next pair (trigger adapter or event set processor group adapter) available to processing. <p> Not-Blocking. </p> Returns
     * {@code null} if currently there is no pair (trigger adapter or event set processor group adapter) available to processing.
     *
     * @return the next pair (trigger adapter or event set processor group adapter) available to processing or {@code null} if none found.
     */
    protected Pair<T, Event> doGetNonBlocking() {
        internalLock.lock();

        try {
            // Names of non-singletons that are waiting in this queue for an earlier event. This is used to preserve the order of events.
            Set<String> nonSingletonsWaitningForEarlierEvent = new HashSet<>();

            // Search the internal entry list starting from the the oldest entries.
            ListIterator<Pair<T, Event>> iterator = entries.listIterator();
            while (iterator.hasNext()) {
                Pair<T, Event> entry = iterator.next();
                T adapter = entry.getLeft();
                Event event = entry.getRight();

                if (adapter.getDefinition().isSingleton()) {
                    // Singletons (in this case triggers) are never blocked and next event could be processes concurrently.
                    iterator.remove();

                    return entry;
                } else {
                    // Non-singletons (in this case rule group adapters and correlator group adapters) are blocked while processing
                    // the previous event.
                    // Note that only the earliest event going to the given adapter should be considered.
                    if (!currentlyProcessedNonSingletons.contains(adapter.getName())
                            && !nonSingletonsWaitningForEarlierEvent.contains(adapter.getName())) {
                        // If necessary verify also event name readiness.
                        if (allowConcurrentEventTypeProcessing || !currentlyProcessedEventNames.contains(event.getName())) {
                            currentlyProcessedNonSingletons.add(adapter.getName());
                            currentlyProcessedEventNames.add(event.getName());
                            iterator.remove();

                            return entry;
                        }
                    }

                    nonSingletonsWaitningForEarlierEvent.add(adapter.getName());
                }
            }

            return null;
        } finally {
            internalLock.unlock();
        }
    }

    /**
     * Releases trigger adapter or event set processor group adapter after the completion of processing the event.
     *
     * @param entry an entry.
     */
    public void release(Pair<T, Event> entry) {
        lock.lock();

        try {
            internalLock.lock();
            logger.debug("Release: {}", entry);
            currentlyProcessedNonSingletons.remove(entry.getLeft().getName());
            currentlyProcessedEventNames.remove(entry.getRight().getName());
            lockCondition.signal();
        } finally {
            internalLock.unlock();
            lock.unlock();
        }
    }

    @Override
    public void onProcessorAdded(T eventProcessorAdapter) {
        // Nothing to do.
    }

    @Override
    public void onProcessorRemoved(T eventProcessorAdapter) {
        // Nothing to do.
    }

    public int getSize() {
        return entries.size();
    }
}
