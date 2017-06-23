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

package org.openksavi.sponge.core.engine.processing;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.engine.Engine;
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

    /** Input queue. */
    private EventQueue inQueue;

    /** Output queue. */
    private EventQueue outQueue;

    /** Event map (event name, registered processor adapters). */
    private Map<String, Set<AtomicReference<T>>> eventMap = Collections.synchronizedMap(new HashMap<>());

    /** Registered processor map (processor name, processor adapter). */
    private Map<String, AtomicReference<T>> registeredProcessorAdapterMap = Collections.synchronizedMap(new LinkedHashMap<>());

    protected EventProcessorRegistrationListener<T> eventProcessorRegistrationListener;

    /** Synchronization lock. */
    protected Lock lock = new ReentrantLock(true);

    /**
     * Creates a new processing unit.
     *
     * @param name
     *            name.
     * @param engine
     *            the engine.
     * @param inQueue
     *            input queue.
     * @param outQueue
     *            output queue.
     */
    public BaseProcessingUnit(String name, Engine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    protected Map<String, Set<AtomicReference<T>>> getEventMap() {
        return eventMap;
    }

    public abstract class LoopWorker implements Runnable {

        private Processable processable;

        protected LoopWorker(Processable processable) {
            this.processable = processable;
        }

        public Processable getProcessable() {
            return processable;
        }

        protected Event getInEvent() throws InterruptedException {
            while (isRunning()) {
                Event event = getInQueue().get(GET_ITERATION_TIMEOUT);
                if (event != null) {
                    return event;
                }
            }

            return null;
        }

        public abstract boolean runIteration() throws InterruptedException;

        @Override
        public final void run() {
            while (!Thread.currentThread().isInterrupted()) {
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
            if (!getEngine().isRunning()) {
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

    /**
     * Returns an event map.
     *
     * @param eventName
     *            an event name.
     *
     * @return an event map.
     */
    protected Set<AtomicReference<T>> getEventProcessors(String eventName) {
        lock.lock();
        try {
            Set<AtomicReference<T>> internalList = eventMap.get(eventName);
            return internalList != null ? internalList// internalList.stream().map(AtomicReference::get).collect(Collectors.toList())
                    : Collections.emptySet();
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
     * @param processor
     *            processor.
     */
    @Override
    public void addProcessor(T processor) {
        lock.lock();
        try {
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
                        eventMap.get(oldEventName).remove(registered);
                    }
                }

                registered.set(processor);

                processorChanged(oldProcessor, processor);
            } else {
                registered = new AtomicReference<>(processor);
                registeredProcessorAdapterMap.put(processor.getName(), registered);
            }

            for (String eventName : processor.getEventNames()) {
                Set<AtomicReference<T>> processorList = eventMap.get(eventName);
                if (processorList == null) {
                    processorList = new CopyOnWriteArraySet<>();
                    eventMap.put(eventName, processorList);
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
     * Removes processor specified by its name.
     *
     * @param name
     *            processor name.
     */
    @Override
    public void removeProcessor(String name) {
        try {
            lock.lock();

            eventMap.values().forEach(eventProcessors -> removeProcessor(eventProcessors, name, true));

            registeredProcessorAdapterMap.remove(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes all processors.
     */
    @Override
    public void removeAllProcessors() {
        lock.lock();
        try {
            for (Set<AtomicReference<T>> processorList : eventMap.values()) {
                processorList.forEach(processor -> processor.get().clear());
            }

            eventMap.clear();

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
            eventMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes processor.
     *
     * @param processorList
     *            processor list.
     * @param removedProcessorName
     *            name of the processor to remove.
     * @param clear
     *            whether to clear the removed processor
     *            (i.e. invoke {@code clear()}).
     */
    private void removeProcessor(Set<AtomicReference<T>> processorList, String removedProcessorName, boolean clear) {
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
}
