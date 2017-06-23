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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.util.Processable;

/**
 * Default Thread Pool Manager.
 */
public class DefaultThreadPoolManager extends BaseEngineModule implements ThreadPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultThreadPoolManager.class);

    /** Map of (processable, ExecutorEntry). */
    protected Map<Processable, ExecutorEntry> processableExecutors = Collections.synchronizedMap(new LinkedHashMap<>());

    /** Map of (name, ExecutorEntry). */
    protected Map<String, ExecutorEntry> namedExecutors = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Executor entry.
     */
    protected static class ExecutorEntry {

        private int threadCount;

        private ExecutorService executor;

        private List<Future<?>> futures = Collections.synchronizedList(new ArrayList<>());

        public ExecutorEntry(int threadCount) {
            this.threadCount = threadCount;
        }

        public ExecutorEntry(int threadCount, ExecutorService executor) {
            this.threadCount = threadCount;
            this.executor = executor;
        }

        /**
         * @return the threadCount
         */
        public int getThreadCount() {
            return threadCount;
        }

        /**
         * @param threadCount
         *            the threadCount to set
         */
        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        /**
         * @return the executor
         */
        public ExecutorService getExecutor() {
            return executor;
        }

        /**
         * @param executor
         *            the executor to set
         */
        public void setExecutor(ExecutorService executor) {
            this.executor = executor;
        }

        /**
         * @return the future
         */
        public List<Future<?>> getFutures() {
            return futures;
        }

        public void clear() {
            executor = null;
            futures.clear();
        }
    }

    /**
     * Creates a new Thread Pool Manager.
     *
     * @param engine
     *            the engine.
     */
    public DefaultThreadPoolManager(Engine engine) {
        super("ThreadPoolManager", engine);
    }

    @Override
    public void startup() {
        processableExecutors.forEach((processable, executorEntry) -> startupExecutor(processable, executorEntry));

        setRunning(true);
    }

    private void startupExecutor(Processable processable, ExecutorEntry executorEntry) {
        int threadCount = executorEntry.getThreadCount();

        ExecutorService executor = /* MoreExecutors.getExitingExecutorService( */createFixedExecutor(processable, threadCount);
        // EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        executorEntry.setExecutor(executor);

        for (int i = 0; i < threadCount; i++) {
            executorEntry.getFutures().add(executor.submit(processable.createWorker()));
        }
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        processableExecutors.forEach((processable, executorEntry) -> shutdownExecutor(processable, executorEntry));
        namedExecutors.forEach((name, executorEntry) -> shutdownExecutor(name, executorEntry));
        setRunning(false);
    }

    private void shutdownExecutor(Object named, ExecutorEntry executorEntry) {
        ExecutorService executor = executorEntry.getExecutor();

        if (executor == null) {
            return;
        }

        try {
            executorEntry.getFutures().forEach(future -> future.cancel(true));
            Utils.shutdownExecutorService(getEngine(), named, executor);
        } finally {
            executorEntry.clear();
        }
    }

    @Override
    public void addProcessable(Processable processable) {
        addProcessable(processable, 1);

    }

    @Override
    public void addProcessable(Processable processable, int workers) {
        processableExecutors.put(processable, new ExecutorEntry(workers));
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
    }

    @Override
    public ExecutorService addMainProcessingUnitWorkerExecutor() {
        return addMainProcessingUnitWorkerExecutor("MainProcessingUnit.WorkerExecutor",
                engine.getConfigurationManager().getMainProcessingUnitThreadCount());
    }

    protected ExecutorService addMainProcessingUnitWorkerExecutor(String name, int workers) {
        if (namedExecutors.containsKey(name)) {
            throw new IllegalArgumentException("Executor named " + name + " already exists.");
        }
        ExecutorService executor = new ThreadPoolExecutor(workers, workers, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(getEngine().getDefaultParameters().getMainProcessingUnitWorkerExecutorQueueSize()),
                createThreadFactory(name));
        namedExecutors.put(name, new ExecutorEntry(workers, executor));

        return executor;
    }

    @Override
    public ExecutorService addAsyncEventSetProcessorExecutor() {
        return addAsyncEventSetProcessorExecutor("MainProcessingUnit.AsyncEventSetExecutor",
                engine.getConfigurationManager().getAsyncEventSetProcessorExecutorThreadCount());
    }

    protected ExecutorService addAsyncEventSetProcessorExecutor(String name, int maxThreadCount) {
        if (namedExecutors.containsKey(name)) {
            throw new IllegalArgumentException("Executor named " + name + " already exists.");
        }
        ExecutorService executor = new ThreadPoolExecutor(Utils.calculateInitialDynamicThreadPoolSize(getEngine(), maxThreadCount),
                maxThreadCount, getEngine().getDefaultParameters().getDynamicThreadPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), createThreadFactory(name));

        namedExecutors.put(name, new ExecutorEntry(maxThreadCount, executor));

        return executor;
    }

    @Override
    public ExecutorService getExecutor(String name) {
        ExecutorEntry executorEntry = namedExecutors.get(name);
        if (executorEntry == null) {
            throw new IllegalArgumentException("Executor named " + name + " doesn't exist.");
        }
        return executorEntry.getExecutor();
    }

    public class WaitRejectedExecutionHandlerPolicy implements RejectedExecutionHandler {

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
