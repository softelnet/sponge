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

import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Engine parameters.
 */
public interface EngineParameters {

    /**
     * Returns the number of the Main Processing Unit worker threads.
     *
     * @return the number of the Main Processing Unit worker threads.
     */
    int getMainProcessingUnitThreadCount();

    /**
     * Returns the number of threads used by processing units to listen to an event queue concurrently.
     *
     * @return the number of threads.
     */
    int getProcessingUnitConcurrentListenerThreadCount();

    /**
     * Returns the event queue capacity.
     *
     * @return the event queue capacity.
     */
    int getEventQueueCapacity();

    /**
     * Returns the number of threads used by the Event Scheduler.
     *
     * @return the number of threads used by the Event Scheduler.
     */
    int getEventSchedulerThreadCount();

    /**
     * Returns the number of threads used by a duration executor.
     *
     * @return the number of threads used by a duration executor.
     */
    int getDurationThreadCount();

    /**
     * Returns the number of threads used by an event set processor asynchronous executor.
     *
     * @return the number of threads used by an event set processor asynchronous executor.
     */
    int getAsyncEventSetProcessorExecutorThreadCount();

    /**
     * Return event clone policy.
     *
     * @return eventClonePolicy
     */
    EventClonePolicy getEventClonePolicy();

    /**
     * Returns an event set processor default synchronous flag.
     *
     * @return an event set processor default synchronous flag.
     */
    boolean getEventSetProcessorDefaultSynchronous();

    /**
     * Returns an Executor shutdown timeout.
     *
     * @return an Executor shutdown timeout.
     */
    long getExecutorShutdownTimeout();

    /**
     * Returns a specific implementation parameter - an asynchronous event set processor processing partition size.
     *
     * @return a partition size.
     */
    int getAsyncEventSetProcessorProcessingPartitionSize();

    /**
     * Returns a specific implementation parameter - an asynchronous event set processor processing threshold.
     *
     * @return a threshold value.
     */
    int getAsyncEventSetProcessorProcessingThreshold();

    /**
     * Returns a flag to allow for concurrent processing of events that have the same type by event set processors.
     *
     * @return a flag to allow for concurrent processing of events that have the same type by event set processors.
     */
    boolean getAllowConcurrentEventTypeProcessingByEventSetProcessors();

    /**
     * Returns {@code true} if auto-enable of processors is turned on.
     *
     * @return {@code true} if auto-enable of processors is turned on.
     */
    boolean getAutoEnable();

    /**
     * Sets the number of the Main Processing Unit worker threads.
     *
     * @param mainProcessingUnitThreadCount the number of the Main Processing Unit worker threads.
     */
    void setMainProcessingUnitThreadCount(int mainProcessingUnitThreadCount);

    /**
     * Sets the number of threads used by processing units to listen to an event queue concurrently.
     *
     * @param processingUnitConcurrentListenerThreadCount the number of threads used by processing units to listen to an event queue
     *        concurrently.
     */
    void setProcessingUnitConcurrentListenerThreadCount(int processingUnitConcurrentListenerThreadCount);

    /**
     * Sets event queue capacity.
     *
     * @param eventQueueCapacity event queue capacity.
     */
    void setEventQueueCapacity(int eventQueueCapacity);

    /**
     * Sets the Event Scheduler thread count.
     *
     * @param eventSchedulerThreadCount the Event Scheduler thread count.
     */
    void setEventSchedulerThreadCount(int eventSchedulerThreadCount);

    /**
     * Sets duration executor thread count.
     *
     * @param durationThreadCount duration executor thread count.
     */
    void setDurationThreadCount(int durationThreadCount);

    /**
     * Sets the number of threads used by an event set processor asynchronous executor.
     *
     * @param asyncEventSetProcessorExecutorThreadCount the number of threads used by an event set processor asynchronous executor.
     */
    void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount);

    /**
     * Sets event clone policy.
     *
     * @param eventClonePolicy event clone policy.
     */
    void setEventClonePolicy(EventClonePolicy eventClonePolicy);

    /**
     * Sets an event set processor default synchronous flag.
     *
     * @param eventSetProcessorDefaultSynchronous an event set processor default synchronous flag.
     */
    void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous);

    /**
     * Sets an Executor shutdown timeout.
     *
     * @param executorShutdownTimeout an Executor shutdown timeout.
     */
    void setExecutorShutdownTimeout(long executorShutdownTimeout);

    /**
     * Sets a specific {link org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler} implementation parameter -
     * an asynchronous processing partition size.
     *
     * @param asyncEventSetProcessorProcessingPartitionSize a partition size.
     */
    void setAsyncEventSetProcessorProcessingPartitionSize(int asyncEventSetProcessorProcessingPartitionSize);

    void setAsyncEventSetProcessorProcessingThreshold(int asyncEventSetProcessorProcessingThreshold);

    /**
     * Sets a flag to allow for concurrent processing of events that have the same type by event set processors.
     *
     * @param allowConcurrentEventTypeProcessingByEventSetProcessors a flag to allow for concurrent processing of events that have the same
     *        type by event set processors.
     */
    void setAllowConcurrentEventTypeProcessingByEventSetProcessors(boolean allowConcurrentEventTypeProcessingByEventSetProcessors);

    /**
     * Sets auto-enable of processors.
     *
     * @param autoEnable auto-enable of processors.
     */
    void setAutoEnable(boolean autoEnable);

    /**
     * Returns a Main Processing Unit worker executor queue size.
     *
     * @return a Main Processing Unit worker executor queue size.
     */
    int getMainProcessingUnitWorkerExecutorQueueSize();

    /**
     * Sets a Main Processing Unit worker executor queue size.
     *
     * @param mainProcessingUnitWorkerExecutorQueueSize a Main Processing Unit worker executor queue size.
     */
    void setMainProcessingUnitWorkerExecutorQueueSize(int mainProcessingUnitWorkerExecutorQueueSize);

    /**
     * Returns internal queue blocking put operation sleep time between retries (in milliseconds).
     *
     * @return internal queue blocking put operation sleep time between retries.
     */
    long getInternalQueueBlockingPutSleep();

    /**
     * Sets internal queue blocking put operation sleep time between retries (in milliseconds).
     *
     * @param internalQueueBlockingPutSleep internal queue blocking put operation sleep time between retries.
     */
    void setInternalQueueBlockingPutSleep(long internalQueueBlockingPutSleep);

    /**
     * Returns a decomposed queue capacity.
     *
     * @return a decomposed queue capacity.
     */
    int getDecomposedQueueCapacity();

    /**
     * Sets a decomposed queue capacity.
     *
     * @param decomposedQueueCapacity a decomposed queue capacity.
     */
    void setDecomposedQueueCapacity(int decomposedQueueCapacity);

    /**
     * Returns a Main Event Queue capacity.
     *
     * @return a Main Event Queue capacity.
     */
    int getMainEventQueueCapacity();

    /**
     * Sets a Main Event Queue capacity.
     *
     * @param mainEventQueueCapacity a Main Event Queue capacity.
     */
    void setMainEventQueueCapacity(int mainEventQueueCapacity);

    /**
     * Returns a dynamic thread pool initial size ratio.
     *
     * @return a dynamic thread pool initial size ratio.
     */
    double getInitialDynamicThreadPoolSizeRatio();

    /**
     * Sets a dynamic thread pool initial size ratio.
     *
     * @param initialDynamicThreadPoolSizeRatio a dynamic thread pool initial size ratio.
     */
    void setInitialDynamicThreadPoolSizeRatio(double initialDynamicThreadPoolSizeRatio);

    /**
     * Returns a dynamic thread pool keep alive time (in milliseconds).
     *
     * @return a dynamic thread pool keep alive time.
     */
    long getDynamicThreadPoolKeepAliveTime();

    /**
     * Sets a dynamic thread pool keep alive time (in milliseconds).
     *
     * @param dynamicThreadPoolKeepAliveTime a dynamic thread pool keep alive time.
     */
    void setDynamicThreadPoolKeepAliveTime(long dynamicThreadPoolKeepAliveTime);
}
