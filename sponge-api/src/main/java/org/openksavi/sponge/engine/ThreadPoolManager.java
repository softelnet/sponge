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

package org.openksavi.sponge.engine;

import org.openksavi.sponge.engine.processing.FilterProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.util.Processable;

/**
 * Thread Pool Manager.
 */
public interface ThreadPoolManager extends EngineModule {

    /**
     * Creates a new thread pool used by the Filter Processing Unit for listening to the Input Event Queue.
     *
     * @param processable a processable.
     * @param workers amount of threads.
     */
    ProcessableThreadPool createFilterProcessingUnitListenerThreadPool(FilterProcessingUnit filterProcessingUnit);

    /**
     * Creates a new thread pool used by the Main Processing Unit for listening to the Main Event Queue.
     *
     * @param processable a processable.
     * @param workers amount of threads.
     */
    ProcessableThreadPool createMainProcessingUnitListenerThreadPool(MainProcessingUnit mainProcessingUnit);

    /**
     * Creates a new thread pool used by the Main Processing Unit for listening to the decomposed queue.
     *
     * @param processable a processable.
     */
    ProcessableThreadPool createMainProcessingUnitDecomposedQueueThreadPool(Processable processable);

    /**
     * Creates a new thread pool used by the Main Processing Unit for worker threads.
     *
     * @return a thread pool.
     */
    ThreadPool createMainProcessingUnitWorkerThreadPool();

    /**
     * Creates a new thread pool used by the Main Processing Unit for asynchronous processing of event set processors.
     *
     * @return a thread pool.
     */
    ThreadPool createMainProcessingUnitAsyncEventSetProcessorThreadPool();

    /**
     * Creates a new thread pool used by the Main Processing Unit for event set processors duration.
     *
     * @return a thread pool.
     */
    ThreadPool createMainProcessingUnitEventSetProcessorDurationThreadPool();

    /**
     * Starts up a processable thread pool.
     *
     * @param threadPool the thread pool.
     */
    void startupProcessableThreadPool(ProcessableThreadPool threadPool);

    /**
     * Shuts down a thread pool.
     *
     * @param threadPool the thread pool.
     * @param mayInterruptIfRunning allow to interrupt.
     */
    void shutdownThreadPool(ThreadPool threadPool, boolean mayInterruptIfRunning);
}
