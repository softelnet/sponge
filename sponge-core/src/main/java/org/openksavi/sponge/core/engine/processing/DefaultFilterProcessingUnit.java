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

import java.util.concurrent.atomic.AtomicReference;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessableThreadPool;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.filter.FilterAdapter;
import org.openksavi.sponge.util.Processable;

/**
 * Default Filter Processing Unit.
 */
public class DefaultFilterProcessingUnit extends BaseProcessingUnit<FilterAdapter> implements FilterProcessingUnit {

    /** The thread pool used by the Filter Processing Unit for listening to the Input Event Queue. */
    protected ProcessableThreadPool filterProcessingUnitListenerThreadPoolEntry;

    /**
     * Creates a new Filter Processing Unit.
     *
     * @param name processing unit name.
     * @param engine the engine.
     * @param inQueue input event queue.
     * @param outQueue output event queue.
     */
    public DefaultFilterProcessingUnit(String name, Engine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine, inQueue, outQueue);
    }

    @Override
    public void doStartup() {
        filterProcessingUnitListenerThreadPoolEntry = getEngine().getThreadPoolManager().createFilterProcessingUnitListenerThreadPool(this);
        getEngine().getThreadPoolManager().startupProcessableThreadPool(filterProcessingUnitListenerThreadPoolEntry);
    }

    @Override
    public void doShutdown() {
        getEngine().getThreadPoolManager().shutdownThreadPool(filterProcessingUnitListenerThreadPoolEntry, true);
    }

    /**
     * Creates the worker.
     *
     * @return the worker.
     */
    @Override
    public Runnable createWorker() {
        return new FilterLoopWorker(this);
    }

    /**
     * Processes the specified event.
     *
     * @param event event.
     * @return {@code true} if this event should be put in the output queue.
     */
    protected boolean processEvent(Event event) {
        for (AtomicReference<FilterAdapter> filterContextR : getEventProcessors(event.getName())) {
            FilterAdapter filterContext = filterContextR.get();
            try {
                if (!runFilter(filterContext, event)) {
                    return false;
                }
            } catch (Exception e) {
                getEngine().handleError(filterContext, e);
            }
        }

        return true;
    }

    /**
     * Runs a filter.
     *
     * @param filterContext a filter context.
     * @param event an event.
     * @return {@code true} if the filter accepts the specified event.
     */
    protected boolean runFilter(FilterAdapter filterContext, Event event) {
        return filterContext.getProcessor().accepts(event);
    }

    /**
     * Processing unit worker to be used in a thread pool.
     */
    protected class FilterLoopWorker extends LoopWorker {

        public FilterLoopWorker(Processable processable) {
            super(processable);
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            // Get an event from the input queue (blocking operation).
            Event event = getInEvent();

            if (event == null) {
                return false;
            }

            // First, process the event.
            if (processEvent(event)) {
                // Then put the event in the output queue
                getOutQueue().put(event);
            }

            return true;
        }
    }

    @Override
    public boolean supportsConcurrentListenerThreadPool() {
        return false;
    }
}
