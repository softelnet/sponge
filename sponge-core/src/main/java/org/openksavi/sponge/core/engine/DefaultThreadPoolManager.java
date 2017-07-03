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

package org.openksavi.sponge.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.util.Processable;

/**
 * Default Thread Pool Manager.
 */
public class DefaultThreadPoolManager extends BaseEngineModule implements ThreadPoolManager {

    /** The thread pool used by the Filter Processing Unit for listening to the Input Event Queue. */
    protected ProcessableExecutorEntry filterProcessingUnitListenerThreadPoolEntry;

    /** The thread pool used by the Main Processing Unit for listening to the Main Event Queue. */
    protected ProcessableExecutorEntry mainProcessingUnitListenerThreadPoolEntry;

    /** The thread pool used by the Main Processing Unit for listening to the decomposed queue. */
    protected ProcessableExecutorEntry mainProcessingUnitDecomposedQueueThreadPoolEntry;

    /** The thread pool used by the Main Processing Unit for worker threads. */
    protected ExecutorEntry mainProcessingUnitWorkerThreadPoolEntry;

    /** The thread pool used by the Main Processing Unit for asynchronous processing of event set processors. */
    protected ExecutorEntry mainProcessingUnitAsyncEventSetProcessorThreadPoolEntry;

    /** Lock. */
    protected Lock lock = new ReentrantLock();

    /**
     * Executor entry.
     */
    protected static class ExecutorEntry {

        private String name;

        private ExecutorService executor;

        private List<Future<?>> futures = Collections.synchronizedList(new ArrayList<>());

        public ExecutorEntry(String name, ExecutorService executor) {
            this.name = name;
            this.executor = executor;
        }

        public String getName() {
            return name;
        }

        /**
         * Returns the executor.
         *
         * @return the executor.
         */
        public ExecutorService getExecutor() {
            return executor;
        }

        /**
         * Sets an executor.
         *
         * @param executor the executor to set.
         */
        public void setExecutor(ExecutorService executor) {
            this.executor = executor;
        }

        /**
         * Returns the future.
         *
         * @return the future.
         */
        public List<Future<?>> getFutures() {
            return futures;
        }

        /**
         * Clears this entry.
         */
        public void clear() {
            executor = null;
            futures.clear();
        }
    }

    protected static class ProcessableExecutorEntry extends ExecutorEntry {

        private Processable processable;

        public ProcessableExecutorEntry(ExecutorService executor, Processable processable) {
            super(processable.toString(), executor);

            this.processable = processable;
        }

        public Processable getProcessable() {
            return processable;
        }

        public void setProcessable(Processable processable) {
            this.processable = processable;
        }
    }

    /**
     * Creates a new Thread Pool Manager.
     *
     * @param engine the engine.
     */
    public DefaultThreadPoolManager(Engine engine) {
        super("ThreadPoolManager", engine);
    }

    @Override
    public void startup() {
        lock.lock();
        try {
            if (isRunning()) {
                return;
            }

            startupProcessableExecutor(mainProcessingUnitDecomposedQueueThreadPoolEntry);
            startupProcessableExecutor(mainProcessingUnitListenerThreadPoolEntry);

            // The Filter Processing Unit thread pool should be started as the last.
            startupProcessableExecutor(filterProcessingUnitListenerThreadPoolEntry);

            setRunning(true);
        } finally {
            lock.unlock();
        }
    }

    private void startupProcessableExecutor(ProcessableExecutorEntry executorEntry) {
        executorEntry.getFutures().add(executorEntry.getExecutor().submit(executorEntry.getProcessable().createWorker()));
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (!isRunning()) {
                return;
            }

            // Stop processable thread pools.
            shutdownExecutor(filterProcessingUnitListenerThreadPoolEntry, true);
            shutdownExecutor(mainProcessingUnitListenerThreadPoolEntry, true);
            shutdownExecutor(mainProcessingUnitDecomposedQueueThreadPoolEntry, true);

            // Stop other thread pools here too.
            shutdownExecutor(mainProcessingUnitWorkerThreadPoolEntry, false);
            shutdownExecutor(mainProcessingUnitAsyncEventSetProcessorThreadPoolEntry, false);

            setRunning(false);
        } finally {
            lock.unlock();
        }
    }

    private void shutdownExecutor(ExecutorEntry executorEntry, boolean mayInterruptIfRunning) {
        ExecutorService executor = executorEntry.getExecutor();

        if (executor == null) {
            return;
        }

        try {
            executorEntry.getFutures().forEach(future -> future.cancel(mayInterruptIfRunning));
            Utils.shutdownExecutorService(getEngine(), executorEntry.getName(), executor);
        } finally {
            executorEntry.clear();
        }
    }

    @Override
    public void createFilterProcessingUnitListenerThreadPool(Processable processable, int workers) {
        filterProcessingUnitListenerThreadPoolEntry = new ProcessableExecutorEntry(createFixedExecutor(processable, workers), processable);
    }

    @Override
    public void createMainProcessingUnitListenerThreadPool(Processable processable, int workers) {
        mainProcessingUnitListenerThreadPoolEntry = new ProcessableExecutorEntry(createFixedExecutor(processable, workers), processable);
    }

    @Override
    public void createMainProcessingUnitDecomposedQueueThreadPool(Processable processable) {
        mainProcessingUnitDecomposedQueueThreadPoolEntry = new ProcessableExecutorEntry(createFixedExecutor(processable, 1), processable);
    }

    @Override
    public void pause() {
        // Simplified implementation.
        shutdown();
    }

    @Override
    public void resume() {
        // Simplified implementation.
        startup();
    }

    protected BasicThreadFactory createThreadFactory(Object named) {
        return new BasicThreadFactory.Builder().namingPattern(named.toString() + "-%d").build();
    }

    protected ExecutorService createFixedExecutor(Object named, int threadCount) {
        return Executors.newFixedThreadPool(threadCount, createThreadFactory(named));
        // ExecutorService executor = /* MoreExecutors.getExitingExecutorService( */createFixedExecutor(processable, threadCount);
        // // EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public ExecutorService createMainProcessingUnitWorkerThreadPool() {
        return addMainProcessingUnitWorkerExecutor("MainProcessingUnit.Worker",
                getEngine().getConfigurationManager().getMainProcessingUnitThreadCount());
    }

    protected ExecutorService addMainProcessingUnitWorkerExecutor(String name, int workers) {
        ExecutorService executor = new ThreadPoolExecutor(workers, workers, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(getEngine().getDefaultParameters().getMainProcessingUnitWorkerExecutorQueueSize()),
                createThreadFactory(name));

        mainProcessingUnitWorkerThreadPoolEntry = new ExecutorEntry(name, executor);

        return executor;
    }

    @Override
    public ExecutorService createMainProcessingUnitAsyncEventSetProcessorThreadPool() {
        return addMainProcessingUnitAsyncEventSetProcessorExecutor("MainProcessingUnit.AsyncEventSet",
                getEngine().getConfigurationManager().getAsyncEventSetProcessorExecutorThreadCount());
    }

    protected ExecutorService addMainProcessingUnitAsyncEventSetProcessorExecutor(String name, int maxThreadCount) {
        ExecutorService executor = new ThreadPoolExecutor(Utils.calculateInitialDynamicThreadPoolSize(getEngine(), maxThreadCount),
                maxThreadCount, getEngine().getDefaultParameters().getDynamicThreadPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), createThreadFactory(name));

        mainProcessingUnitAsyncEventSetProcessorThreadPoolEntry = new ExecutorEntry(name, executor);

        return executor;
    }

    public static class WaitRejectedExecutionHandlerPolicy implements RejectedExecutionHandler {

        public WaitRejectedExecutionHandlerPolicy() {
            //
        }

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().put(runnable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Interrupted", e);
                }
            } else {
                throw new RejectedExecutionException("Executor has been shut down");
            }
        }
    }
}
