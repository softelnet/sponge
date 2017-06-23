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

package org.openksavi.sponge.core.engine.processing.decomposed;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.core.engine.processing.BaseMainProcessingUnit;
import org.openksavi.sponge.core.event.ProcessorControlEvent;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.util.Processable;

/**
 * Main processing unit that handles triggers, rules and aggregators.
 */
public class DecomposedQueueMainProcessingUnit extends BaseMainProcessingUnit {

    private static final Logger logger = LoggerFactory.getLogger(DecomposedQueueMainProcessingUnit.class);

    /** Decomposed custom queue of entries (trigger adapter or event set processor group adapter, event). */
    private DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue;

    /** Thread pool for processing an event by a single trigger or a single event set processor group. */
    private ExecutorService workerExecutor;

    /**
     * Creates a new main processing unit.
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
    public DecomposedQueueMainProcessingUnit(String name, Engine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine, inQueue, outQueue);

        setDecomposedQueue(
                new DecomposedQueue<>(engine.getDefaultParameters().getAllowConcurrentEventTypeProcessingByEventSetProcessors()));
    }

    public void setDecomposedQueue(DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue) {
        this.decomposedQueue = decomposedQueue;
        setEventProcessorRegistrationListener(decomposedQueue);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void startup() {
        if (isRunning()) {
            return;
        }

        startupHandlers();

        startupGroupExecutor();

        // One thread for reading from the decomposed queue.
        engine.getThreadPoolManager().addProcessable(new DecomposedQueueReaderProcessable());

        workerExecutor = engine.getThreadPoolManager().addMainProcessingUnitWorkerExecutor();

        setRunning(true);
    }

    /**
     * Creates the worker.
     *
     * @return the worker.
     */
    @Override
    public Runnable createWorker() {
        return new DecomposedQueueWriterLoopWorker(this);
    }

    protected boolean supportsControlEventForProcessor(ProcessorAdapter<?> processorAdapter) {
        ProcessorType type = processorAdapter.getType();

        return type == ProcessorType.TRIGGER || type == ProcessorType.RULE || type == ProcessorType.AGGREGATOR ||
                type == ProcessorType.RULE_GROUP || type == ProcessorType.AGGREGATOR_GROUP;
    }

    /**
     * Processes an event.
     *
     * @param event
     *            an event.
     *
     * @return {@code true} if the event has been processed by at least one adapter.
     */
    protected boolean processEvent(Event event) {
        if (event instanceof ControlEvent) {
            if (event instanceof ProcessorControlEvent) {
                ProcessorAdapter<?> processorAdapter = ((ProcessorControlEvent) event).getProcessorAdapter();
                if (processorAdapter instanceof EventProcessorAdapter && supportsControlEventForProcessor(processorAdapter)) {
                    decomposedQueue.put(new ImmutablePair<>((EventProcessorAdapter<?>) processorAdapter, event));
                }
            }

            return true;
        }

        Set<AtomicReference<EventProcessorAdapter<?>>> adapterRs = getEventProcessors(event.getName());
        adapterRs.forEach(adapterR -> decomposedQueue.put(new ImmutablePair<>(adapterR.get(), event)));

        return !adapterRs.isEmpty();
    }

    protected class DecomposedQueueWriterLoopWorker extends LoopWorker {

        public DecomposedQueueWriterLoopWorker(Processable processable) {
            super(processable);
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            // Get an event from the input queue (blocking operation).
            Event event = getInEvent();

            if (event == null) {
                return false;
            }

            // Add a decomposed entry (trigger adapter or event set processor group adapter, event) to the decomposed queue.
            if (!processEvent(event)) {
                // If the event isn't listened to by any event processor then put the event in the output queue.
                getOutQueue().put(event);
            }

            return true;
        }
    }

    public class DecomposedQueueReaderProcessable implements Processable {

        @Override
        public Runnable createWorker() {
            return new DecomposedQueueReaderWorker(this);
        }

        @Override
        public String toString() {
            return getName() + ".DecomposedQueueReader";
        }
    }

    protected class DecomposedQueueReaderWorker extends LoopWorker {

        public DecomposedQueueReaderWorker(DecomposedQueueReaderProcessable processable) {
            super(processable);
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            try {
                while (isRunning()) {
                    final Pair<EventProcessorAdapter<?>, Event> entry = decomposedQueue.get(GET_ITERATION_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        final EventProcessorAdapter<?> adapter = entry.getLeft();
                        final Event event = entry.getRight();

                        // Process an event by the adapter asynchronously in a thread from a thread pool.
                        CompletableFuture.runAsync(() -> getHandler(adapter.getType()).processEvent(adapter, event), workerExecutor)
                                .handle((result, exception) -> {
                                    decomposedQueue.release(entry);
                                    return null;
                                });

                        return true;
                    }
                }

                return false;
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                Throwable rootCause = ExceptionUtils.getRootCause(e);
                if (rootCause != null && rootCause instanceof InterruptedException) {
                    throw (InterruptedException) rootCause;
                }

                engine.handleError("runIteration", e);
                return true;
            }
        }
    }

    @Override
    public boolean supportsConcurrentListenerThreadPool() {
        return false;
    }
}
