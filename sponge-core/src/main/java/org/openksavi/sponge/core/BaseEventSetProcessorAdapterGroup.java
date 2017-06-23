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

package org.openksavi.sponge.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.core.event.DurationControlEvent;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;

/**
 * Event set processor adapter group.
 */
@SuppressWarnings("rawtypes")
public abstract class BaseEventSetProcessorAdapterGroup<T extends EventSetProcessorAdapter> extends BaseEventProcessorAdapter
        implements EventSetProcessorAdapterGroup<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseEventSetProcessorAdapterGroup.class);

    /** List of event set processor adapters. */
    private List<T> eventSetProcessorAdapters = Collections.synchronizedList(new LinkedList<>());

    /** Event set processor main processing handler. */
    private EventSetProcessorMainProcessingUnitHandler<EventSetProcessorAdapterGroup<T>, T> handler;

    /** Synchronization lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new event set processor adapter group.
     *
     * @param processorDefinition
     *            an event set processor definition.
     * @param handler
     *            a processing unit handler.
     */
    protected BaseEventSetProcessorAdapterGroup(BaseEventSetProcessorDefinition processorDefinition,
            EventSetProcessorMainProcessingUnitHandler<EventSetProcessorAdapterGroup<T>, T> handler) {
        super(processorDefinition);
        this.handler = handler;
    }

    @Override
    public BaseEventSetProcessorDefinition getDefinition() {
        return (BaseEventSetProcessorDefinition) super.getDefinition();
    }

    /**
     * Returns all event set processor adapters that belong to this group.
     *
     * @return all event set processor adapters that belong to this group.
     */
    @Override
    public List<T> getEventSetProcessorAdapters() {
        return eventSetProcessorAdapters;
    }

    protected abstract T createNewEventSetProcessorAdapter();

    /**
     * Tries to add a new event set processor adapter to this group.
     *
     * @param event
     *            an event.
     */
    @SuppressWarnings("unchecked")
    protected void tryAddNewEventSetProcessor(Event event) {
        String name = getDefinition().getName();
        try {
            T adapter = createNewEventSetProcessorAdapter();

            EventSetProcessor eventSetProcessor = getKnowledgeBase().getEngineOperations().getEngine().getProcessorManager()
                    .createProcessorInstance(getDefinition(), EventSetProcessor.class);

            adapter.setProcessor(eventSetProcessor);
            adapter.setGroup(this);

            // Invoke init on the event set processor instance.
            adapter.getProcessor().init();

            if (!adapter.acceptsAsFirst(event)) {
                return;
            }

            eventSetProcessorAdapters.add(adapter);
            handler.addDuration(adapter);

            logger.debug("{} - Adding new, hash: {}", adapter.getName(), adapter.hashCode());
        } catch (Exception e) {
            throw Utils.wrapException(name, e);
        }
    }

    /**
     * Removes stopped event set processors within this group.
     */
    protected void removeStoppedEventSetProcessors() {
        for (ListIterator<T> iterator = eventSetProcessorAdapters.listIterator(); iterator.hasNext();) {
            T adapter = iterator.next();
            if (!adapter.isRunning()) {
                iterator.remove();
                handler.removeDuration(adapter);
            }
        }
    }

    public abstract boolean needNewInstance(Event event);

    @SuppressWarnings("unchecked")
    protected boolean processControlEvent(Event event) {
        if (!(event instanceof ControlEvent)) {
            return false;
        }

        if (event instanceof DurationControlEvent) {
            durationOccurred((T) ((DurationControlEvent) event).getEventSetProcessorAdapter());
        }

        return true;
    }

    /**
     * Processes a specified event.
     *
     * @param event
     *            event.
     */
    @Override
    public void processEvent(Event event) {
        lock.lock();
        try {
            if (processControlEvent(event)) {
                return;
            }

            if (needNewInstance(event)) {
                tryAddNewEventSetProcessor(event);
            }

            handler.processEventForEventSetProcessorAdapters(getDefinition(), eventSetProcessorAdapters, event);

            removeStoppedEventSetProcessors();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears this aggregator group.
     */
    @Override
    public void clear() {
        lock.lock();
        try {
            eventSetProcessorAdapters.forEach(adapter -> adapter.clear());
            eventSetProcessorAdapters.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void durationOccurred(T adapter) {
        lock.lock();
        try {
            adapter.durationOccurred();
            eventSetProcessorAdapters.remove(adapter);
        } finally {
            lock.unlock();
        }
    }
}
