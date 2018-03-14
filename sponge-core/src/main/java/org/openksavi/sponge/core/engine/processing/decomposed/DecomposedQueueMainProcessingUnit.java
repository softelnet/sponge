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

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.core.engine.processing.BaseMainProcessingUnit;
import org.openksavi.sponge.core.event.ProcessorControlEvent;
import org.openksavi.sponge.engine.ProcessableThreadPool;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.util.Processable;

/**
 * Main processing unit that handles triggers, rules and correlators.
 */
public class DecomposedQueueMainProcessingUnit extends BaseMainProcessingUnit {

    /** The thread pool used by the Main Processing Unit for listening to the Main Event Queue. */
    protected ProcessableThreadPool listenerThreadPool;

    /** The thread pool used by the Main Processing Unit for listening to the decomposed queue. */
    protected ProcessableThreadPool decomposedQueueThreadPool;

    /** The thread pool used by the Main Processing Unit for worker threads. */
    protected ThreadPool workerThreadPool;

    /** The thread pool used by the Main Processing Unit for asynchronous processing of event set processors. */
    protected ThreadPool asyncEventSetProcessorThreadPool;

    /** Decomposed custom queue of entries (trigger adapter or event set processor group adapter, event). */
    private DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue;

    /**
     * Creates a new main processing unit.
     *
     * @param name name.
     * @param engine the engine.
     * @param inQueue input queue.
     * @param outQueue output queue.
     */
    public DecomposedQueueMainProcessingUnit(String name, SpongeEngine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine, inQueue, outQueue);

        setDecomposedQueue(new DecomposedQueue<>(engine.getDefaultParameters().getDecomposedQueueCapacity(),
                engine.getDefaultParameters().getAllowConcurrentEventTypeProcessingByEventSetProcessors()));
    }

    public void setDecomposedQueue(DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue) {
        this.decomposedQueue = decomposedQueue;
        setEventProcessorRegistrationListener(decomposedQueue);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        startupHandlers();

        asyncEventSetProcessorThreadPool = getThreadPoolManager().createMainProcessingUnitAsyncEventSetProcessorThreadPool();
        workerThreadPool = getThreadPoolManager().createMainProcessingUnitWorkerThreadPool();

        // One thread for reading from the decomposed queue.
        decomposedQueueThreadPool =
                getThreadPoolManager().createMainProcessingUnitDecomposedQueueThreadPool(new DecomposedQueueReaderProcessable());
        listenerThreadPool = getThreadPoolManager().createMainProcessingUnitListenerThreadPool(this);

        getThreadPoolManager().startupProcessableThreadPool(decomposedQueueThreadPool);
        getThreadPoolManager().startupProcessableThreadPool(listenerThreadPool);
    }

    @Override
    public void doShutdown() {
        getThreadPoolManager().shutdownThreadPool(listenerThreadPool);
        getThreadPoolManager().shutdownThreadPool(decomposedQueueThreadPool);

        getThreadPoolManager().shutdownThreadPool(workerThreadPool);
        getThreadPoolManager().shutdownThreadPool(asyncEventSetProcessorThreadPool);

        shutdownHandlers();
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

        return type == ProcessorType.TRIGGER || type == ProcessorType.RULE || type == ProcessorType.CORRELATOR
                || type == ProcessorType.RULE_GROUP || type == ProcessorType.CORRELATOR_GROUP;
    }

    /**
     * Processes an event. Adds a decomposed entry (trigger adapter or event set processor group adapter, event) to the decomposed queue.
     *
     * @param event an event.
     *
     * @return {@code true} if the event hasn't been processed by any adapters and should be put into the Output Queue.
     * @throws java.lang.InterruptedException if interrupted.
     */
    public boolean processEvent(Event event) throws InterruptedException {
        if (event instanceof ControlEvent) {
            if (event instanceof ProcessorControlEvent) {
                ProcessorAdapter<?> processorAdapter = ((ProcessorControlEvent) event).getProcessorAdapter();
                if (processorAdapter instanceof EventProcessorAdapter && supportsControlEventForProcessor(processorAdapter)) {
                    putIntoDecomposedQueue(new ImmutablePair<>((EventProcessorAdapter<?>) processorAdapter, event));
                }
            }

            return false;
        } else {
            getEngine().getStatisticsManager().startTimeMeasurementIfNotStartedYet();

            Set<AtomicReference<EventProcessorAdapter<?>>> adapterRs = getEventProcessors(event.getName());
            for (AtomicReference<EventProcessorAdapter<?>> adapterR : adapterRs) {
                putIntoDecomposedQueue(new ImmutablePair<>(adapterR.get(), event));
            }

            getEngine().getStatisticsManager().incrementTimeMeasurementEventCount();

            return adapterRs.isEmpty();
        }
    }

    protected void putIntoDecomposedQueue(Pair<EventProcessorAdapter<?>, Event> entry) throws InterruptedException {
        while (!decomposedQueue.put(entry)) {
            // If decomposed queue is full, than try again after sleep.
            TimeUnit.MILLISECONDS.sleep(getEngine().getDefaultParameters().getInternalQueueBlockingPutSleep());
        }
    }

    protected class DecomposedQueueWriterLoopWorker extends EventLoopWorker {

        public DecomposedQueueWriterLoopWorker(Processable processable) {
            super(processable);
        }

        @Override
        public boolean shouldContinueLoop() {
            // When shutting down, process all events remaining in the Main Input Queue.
            return (isNewOrStartingOrRunning() || isStopping() && getLastEvent() != null) && !Thread.currentThread().isInterrupted();
        }

        @Override
        public boolean processEvent(Event event) throws InterruptedException {
            return DecomposedQueueMainProcessingUnit.this.processEvent(event);
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

        private Pair<EventProcessorAdapter<?>, Event> lastEntry;

        public DecomposedQueueReaderWorker(DecomposedQueueReaderProcessable processable) {
            super(processable);
        }

        @Override
        public boolean shouldContinueLoop() {
            // When shutting down, process all entries remaining in the Decomposed Queue.
            return (isNewOrStartingOrRunning() || isStopping() && lastEntry != null) && !Thread.currentThread().isInterrupted();
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            try {
                while (shouldContinueLoop()) {
                    final Pair<EventProcessorAdapter<?>, Event> entry = decomposedQueue.get(GET_ITERATION_TIMEOUT, TimeUnit.MILLISECONDS);
                    lastEntry = entry;
                    if (entry != null) {
                        final EventProcessorAdapter<?> adapter = entry.getLeft();
                        final Event event = entry.getRight();

                        while (true) {
                            try {
                                // Process an event by the adapter asynchronously in a thread from a thread pool.
                                CompletableFuture.runAsync(() -> {
                                    try {
                                        getHandler(adapter.getType()).processEvent(adapter, event);
                                    } catch (Throwable e) {
                                        getEngine().handleError("WorkerThread", e);
                                    }
                                }, workerThreadPool.getExecutor()).handle((result, exception) -> {
                                    decomposedQueue.release(entry);
                                    return null;
                                });
                                break;
                            } catch (RejectedExecutionException e) {
                                // If rejected because of the lack of free threads, than try again after sleep.
                                TimeUnit.MILLISECONDS.sleep(getEngine().getDefaultParameters().getInternalQueueBlockingPutSleep());
                            }
                        }

                        return true;
                    }
                }

                return false;
            } catch (InterruptedException e) {
                throw e;
            } catch (Throwable e) {
                Throwable rootCause = ExceptionUtils.getRootCause(e);
                if (rootCause != null && rootCause instanceof InterruptedException) {
                    throw (InterruptedException) rootCause;
                }

                getEngine().handleError("runIteration", e);

                return true;
            }
        }
    }

    @Override
    public boolean supportsConcurrentListenerThreadPool() {
        return false;
    }

    public DecomposedQueue<EventProcessorAdapter<?>> getDecomposedQueue() {
        return decomposedQueue;
    }

    @Override
    public ThreadPool getWorkerThreadPool() {
        return workerThreadPool;
    }

    @Override
    public ThreadPool getAsyncEventSetProcessorThreadPool() {
        return asyncEventSetProcessorThreadPool;
    }

    public ProcessableThreadPool getDecomposedQueueThreadPool() {
        return decomposedQueueThreadPool;
    }
}
