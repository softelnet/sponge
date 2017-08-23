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

package org.openksavi.sponge.core.engine.processing;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.ProcessingUnit;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.util.Processable;

/**
 * Base Processing Unit. This is the abstract class for all processing units.
 */
public abstract class BaseProcessingUnit<T extends EventProcessorAdapter<?>> extends BaseEngineModule implements ProcessingUnit<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseProcessingUnit.class);

    public static final long GET_ITERATION_TIMEOUT = 100;

    /** The input queue. */
    private EventQueue inQueue;

    /** The output queue. */
    private EventQueue outQueue;

    /** The map of (event pattern, registered processor adapters listening to this event pattern). */
    private Map<String, Set<AtomicReference<T>>> eventPatternProcessorMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * The cache containing real event names (not patterns) that has occurred. Consists of (eventName -> processors listening to this
     * eventName) pairs.
     */
    private LoadingCache<String, Set<AtomicReference<T>>> eventNameProcessorsCache;

    /** Registered processor map (processor name, processor adapter). */
    private Map<String, AtomicReference<T>> registeredProcessorAdapterMap = Collections.synchronizedMap(new LinkedHashMap<>());

    protected EventProcessorRegistrationListener<T> eventProcessorRegistrationListener;

    /** Synchronization lock. */
    protected Lock lock = new ReentrantLock(true);

    /**
     * Creates a new processing unit.
     *
     * @param name name.
     * @param engine the engine.
     * @param inQueue input queue.
     * @param outQueue output queue.
     */
    public BaseProcessingUnit(String name, Engine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine);
        this.inQueue = inQueue;
        this.outQueue = outQueue;

        eventNameProcessorsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(engine.getDefaultParameters().getProcessingUnitEventProcessorCacheExpireTime(), TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, Set<AtomicReference<T>>>() {

                    @Override
                    public Set<AtomicReference<T>> load(String eventName) throws Exception {
                        return resolveEventProcessors(eventName);
                    }
                });
    }

    public abstract class LoopWorker implements Runnable {

        private Processable processable;

        protected LoopWorker(Processable processable) {
            this.processable = processable;
        }

        public Processable getProcessable() {
            return processable;
        }

        public abstract boolean runIteration() throws InterruptedException;

        public abstract boolean shouldContinueLoop();

        @Override
        public final void run() {
            while (shouldContinueLoop()) {
                try {
                    if (!runIteration()) {
                        return; // Graceful shutdown
                    }
                } catch (InterruptedException e) {
                    if (handleInterruptedException(e)) {
                        break;
                    }
                } catch (Exception e) {
                    Throwable cause = ExceptionUtils.getRootCause(e);
                    if (cause instanceof InterruptedException) {
                        if (handleInterruptedException((InterruptedException) cause)) {
                            break;
                        }
                    } else {
                        getEngine().handleError(getMessageSource(), e);
                    }
                }
            }
        }

        private boolean handleInterruptedException(InterruptedException e) {
            // Expected while shutting down.
            if (isStopping() || isTerminated()) {
                return true;
            } else {
                logger.warn(createMessage("Interrupted"));
            }

            return false;
        }

        private final String createMessage(String msg) {
            return getMessageSource() + ": " + msg;
        }

        private final String getMessageSource() {
            return processable.toString();
        }
    }

    public abstract class EventLoopWorker extends LoopWorker {

        private Event lastEvent;

        public EventLoopWorker(Processable processable) {
            super(processable);
        }

        protected Event getLastEvent() {
            return lastEvent;
        }

        protected Event getInEvent() throws InterruptedException {
            while (shouldContinueLoop()) {
                Event event = getInQueue().get(GET_ITERATION_TIMEOUT);
                lastEvent = event;

                if (event != null) {
                    return event;
                }
            }

            return null;
        }

        /**
         * Returns {@code true} if the event should be put into the out queue.
         *
         * @param event the event.
         * @return {@code true} if the event should be put into the out queue.
         * @throws InterruptedException when the current thread has been interrupted.
         */
        public abstract boolean processEvent(Event event) throws InterruptedException;

        @Override
        public boolean runIteration() throws InterruptedException {
            // Get an event from the input queue (blocking operation).
            Event event = getInEvent();

            if (event == null) {
                return false;
            }

            if (processEvent(event)) {
                getOutQueue().put(event);
            }

            return true;
        }
    }

    protected Set<AtomicReference<T>> resolveEventProcessors(String eventName) {
        lock.lock();
        try {
            Set<AtomicReference<T>> result = new LinkedHashSet<>();

            eventPatternProcessorMap.keySet().stream().filter(pattern -> getEngine().getPatternMatcher().matches(pattern, eventName))
                    .forEach(pattern -> {
                        Set<AtomicReference<T>> internalList = eventPatternProcessorMap.get(pattern);
                        if (internalList != null) {
                            result.addAll(internalList);
                        }
                    });

            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the processors listening for the event.
     *
     * @param eventName the event name.
     *
     * @return the processors listening for the event.
     */
    protected Set<AtomicReference<T>> getEventProcessors(String eventName) {
        lock.lock();
        try {
            return eventNameProcessorsCache.get(eventName);
        } catch (ExecutionException e) {
            throw Utils.wrapException(getClass().getSimpleName(), e.getCause() != null ? e.getCause() : e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns registered processor adapter map.
     *
     * @return registered processor adapter map.
     */
    @Override
    public Map<String, T> getRegisteredProcessorAdapterMap() {
        return registeredProcessorAdapterMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    /**
     * Adds a new processor to this processing unit.
     *
     * @param processor processor.
     */
    @Override
    public void addProcessor(T processor) {
        lock.lock();
        try {
            // Invalidate the cache.
            eventNameProcessorsCache.invalidateAll();

            AtomicReference<T> registered = registeredProcessorAdapterMap.get(processor.getName());

            // Already registered with this name.
            if (registered != null) {
                T oldProcessor = registered.get();
                if (!oldProcessor.getType().equals(processor.getType())) {
                    throw new SpongeException(
                            "An event processor of different type has been already registered with the name: " + processor.getName());
                }

                // Remove associations with events that are no longer listened to by this processor.
                List<String> processorEventNamesList = Arrays.asList(processor.getEventNames());
                for (String oldEventName : oldProcessor.getEventNames()) {
                    if (!processorEventNamesList.contains(oldEventName)) {
                        eventPatternProcessorMap.get(oldEventName).remove(registered);
                    }
                }

                registered.set(processor);

                processorChanged(oldProcessor, processor);
            } else {
                registered = new AtomicReference<>(processor);
                registeredProcessorAdapterMap.put(processor.getName(), registered);
            }

            for (String eventName : processor.getEventNames()) {
                Set<AtomicReference<T>> processorList = eventPatternProcessorMap.get(eventName);
                if (processorList == null) {
                    processorList = new CopyOnWriteArraySet<>();
                    eventPatternProcessorMap.put(eventName, processorList);
                }

                processorList.add(registered);
            }

            if (eventProcessorRegistrationListener != null) {
                eventProcessorRegistrationListener.onProcessorAdded(processor);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void processorChanged(T oldProcessor, T newProcessor) {
        //
    }

    /**
     * Removes all processors.
     */
    @Override
    public void removeAllProcessors() {
        lock.lock();
        try {
            // Invalidate the cache.
            eventNameProcessorsCache.invalidateAll();

            for (Set<AtomicReference<T>> processorList : eventPatternProcessorMap.values()) {
                processorList.forEach(processor -> processor.get().clear());
            }

            eventPatternProcessorMap.clear();

            registeredProcessorAdapterMap.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears unused event mappings.
     */
    protected void clearUnusedEventMapping() {
        lock.lock();
        try {
            // Invalidate the cache.
            eventNameProcessorsCache.invalidateAll();

            eventPatternProcessorMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes processor specified by its name.
     *
     * @param name processor name.
     */
    @Override
    public void removeProcessor(String name) {
        lock.lock();
        try {
            // Invalidate the cache.
            eventNameProcessorsCache.invalidateAll();

            eventPatternProcessorMap.values().forEach(eventProcessors -> doRemoveProcessor(eventProcessors, name, true));

            registeredProcessorAdapterMap.remove(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the processor. Warning: this method doesn't invalidate the cache.
     *
     * @param processorList the processor list.
     * @param removedProcessorName name of the processor to remove.
     * @param clear whether to clear the removed processor (i.e. invoke {@code clear()}).
     */
    private void doRemoveProcessor(Set<AtomicReference<T>> processorList, String removedProcessorName, boolean clear) {
        lock.lock();
        try {
            List<AtomicReference<T>> toRemove = processorList.stream()
                    .filter(processor -> processor.get().getName() != null && processor.get().getName().equals(removedProcessorName))
                    .collect(Collectors.toList());
            toRemove.forEach(processor -> {
                if (clear) {
                    processor.get().clear();
                }
            });

            processorList.removeAll(toRemove);
            if (eventProcessorRegistrationListener != null) {
                toRemove.forEach(processor -> eventProcessorRegistrationListener.onProcessorRemoved(processor.get()));
            }

            registeredProcessorAdapterMap.remove(removedProcessorName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsProcessor(String name) {
        return registeredProcessorAdapterMap.containsKey(name);
    }

    protected EventQueue getInQueue() {
        return inQueue;
    }

    protected EventQueue getOutQueue() {
        return outQueue;
    }

    public EventProcessorRegistrationListener<T> getEventProcessorRegistrationListener() {
        return eventProcessorRegistrationListener;
    }

    public void setEventProcessorRegistrationListener(EventProcessorRegistrationListener<T> eventProcessorRegistrationListener) {
        this.eventProcessorRegistrationListener = eventProcessorRegistrationListener;
    }

    protected ThreadPoolManager getThreadPoolManager() {
        return getEngine().getThreadPoolManager();
    }
}
