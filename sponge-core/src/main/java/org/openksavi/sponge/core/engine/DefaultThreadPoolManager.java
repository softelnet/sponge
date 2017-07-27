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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessableThreadPool;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.util.Processable;

/**
 * Default Thread Pool Manager.
 */
public class DefaultThreadPoolManager extends BaseEngineModule implements ThreadPoolManager {

    /**
     * Creates a new Thread Pool Manager.
     *
     * @param engine the engine.
     */
    public DefaultThreadPoolManager(Engine engine) {
        super("ThreadPoolManager", engine);
    }

    @Override
    public ProcessableThreadPool createFilterProcessingUnitListenerThreadPool(FilterProcessingUnit filterProcessingUnit) {
        int threadCount = filterProcessingUnit.supportsConcurrentListenerThreadPool()
                ? getEngine().getDefaultParameters().getProcessingUnitConcurrentListenerThreadCount() : 1;
        return new DefaultProcessableThreadPool(createFixedExecutor(filterProcessingUnit, threadCount), filterProcessingUnit);
    }

    @Override
    public ProcessableThreadPool createMainProcessingUnitListenerThreadPool(MainProcessingUnit mainProcessingUnit) {
        int threadCount = mainProcessingUnit.supportsConcurrentListenerThreadPool()
                ? getEngine().getDefaultParameters().getProcessingUnitConcurrentListenerThreadCount() : 1;
        return new DefaultProcessableThreadPool(createFixedExecutor(mainProcessingUnit, threadCount), mainProcessingUnit);
    }

    @Override
    public ProcessableThreadPool createMainProcessingUnitDecomposedQueueThreadPool(Processable processable) {
        return new DefaultProcessableThreadPool(createFixedExecutor(processable, 1), processable);
    }

    @Override
    public ThreadPool createMainProcessingUnitWorkerThreadPool() {
        String name = "MainProcessingUnit.Worker";
        int workers = getEngine().getConfigurationManager().getMainProcessingUnitThreadCount();

        ExecutorService executor = new ThreadPoolExecutor(workers, workers, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(getEngine().getDefaultParameters().getMainProcessingUnitWorkerExecutorQueueSize()),
                createThreadFactory(name));

        return new DefaultThreadPool(name, executor);
    }

    @Override
    public ThreadPool createMainProcessingUnitAsyncEventSetProcessorThreadPool() {
        String name = "MainProcessingUnit.AsyncEventSet";
        int maxThreadCount = getEngine().getConfigurationManager().getAsyncEventSetProcessorExecutorThreadCount();

        ExecutorService executor = new ThreadPoolExecutor(Utils.calculateInitialDynamicThreadPoolSize(getEngine(), maxThreadCount),
                maxThreadCount, getEngine().getDefaultParameters().getDynamicThreadPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), createThreadFactory(name));

        return new DefaultThreadPool(name, executor);
    }

    @Override
    public ThreadPool createMainProcessingUnitEventSetProcessorDurationThreadPool() {
        String name = "MainProcessingUnit.Duration";
        ExecutorService executor =
                Executors.newScheduledThreadPool(getEngine().getConfigurationManager().getDurationThreadCount(), createThreadFactory(name));

        return new DefaultThreadPool(name, executor);
    }

    protected BasicThreadFactory createThreadFactory(Object named) {
        return new BasicThreadFactory.Builder().namingPattern(named.toString() + "-%d").build();
    }

    public ExecutorService createFixedExecutor(Object named, int threadCount) {
        return Executors.newFixedThreadPool(threadCount, createThreadFactory(named));
    }

    @Override
    public void startupProcessableThreadPool(ProcessableThreadPool threadPool) {
        threadPool.getFutures().add(threadPool.getExecutor().submit(threadPool.getProcessable().createWorker()));
    }

    @Override
    public void shutdownThreadPool(ThreadPool threadPool) {
        ExecutorService executor = threadPool.getExecutor();

        if (executor == null) {
            return;
        }

        try {
            Utils.shutdownExecutorService(getEngine(), threadPool.getName(), executor);
        } finally {
            threadPool.clear();
        }
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
