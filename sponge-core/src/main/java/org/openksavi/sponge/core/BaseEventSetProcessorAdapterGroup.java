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

package org.openksavi.sponge.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.EventSetProcessorState;
import org.openksavi.sponge.core.event.DurationControlEvent;
import org.openksavi.sponge.core.rule.BaseRuleAdapter;
import org.openksavi.sponge.core.rule.BaseRuleAdapterGroup;
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
    private List<T> eventSetProcessorAdapters = new CopyOnWriteArrayList<>();

    /** Event set processor main processing handler. */
    private EventSetProcessorMainProcessingUnitHandler<EventSetProcessorAdapterGroup<T>, T> handler;

    /** Synchronization lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new event set processor adapter group.
     *
     * @param processorDefinition an event set processor definition.
     * @param handler a processing unit handler.
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
     * @param event an event.
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

            // Invoke init callback on the event set processor instance.
            adapter.getProcessor().onInit();

            if (!adapter.acceptAsFirst(event)) {
                return;
            }

            eventSetProcessorAdapters.add(adapter);
            handler.addDuration(adapter);

            logger.debug("{} - New instance, hash: {}, event: {}", adapter.getName(), adapter.hashCode(), event);
        } catch (Exception e) {
            throw Utils.wrapException(name, e);
        }
    }

    @Override
    public void removeDuration(T adapter) {
        handler.removeDuration(adapter);
    }

    /**
     * Removes finished event set processors within this group.
     */
    protected void removeFinishedEventSetProcessors() {
        Predicate<T> filter = adapter -> adapter.getState() == EventSetProcessorState.FINISHED;
        eventSetProcessorAdapters.stream().filter(filter).forEach(adapter -> handler.removeDuration(adapter));
        eventSetProcessorAdapters.removeIf(filter);
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
     * @param event event.
     */
    @Override
    public void processEvent(Event event) {
        lock.lock();
        try {
            if (processControlEvent(event)) {
                return;
            }

            logger.debug("Processing event: {}", event);

            if (needNewInstance(event)) {
                tryAddNewEventSetProcessor(event);
            }

            logEventTree("before");
            handler.processEventForEventSetProcessorAdapters(getDefinition(), eventSetProcessorAdapters, event);
            logEventTree("after");

            removeFinishedEventSetProcessors();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears this correlator group.
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

    @Override
    public void validate() {
        //
    }

    private void logEventTree(String label) {
        if (logger.isDebugEnabled()) {
            if (this instanceof BaseRuleAdapterGroup) {
                eventSetProcessorAdapters.forEach(adapter -> logger.debug("Event tree " + label + " processing ({}/{}): {}",
                        adapter.getName(), adapter.hashCode(), ((BaseRuleAdapter) adapter).getEventTree()));
            }
        }
    }
}
