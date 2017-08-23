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
     * Returns the number of threads used by processing units to listen to an event queue concurrently. In the current implementation this
     * parameter is not used.
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
     * Returns the event clone policy.
     *
     * @return the event clone policy..
     */
    EventClonePolicy getEventClonePolicy();

    /**
     * Returns the event set processor default synchronous flag.
     *
     * @return the event set processor default synchronous flag.
     */
    boolean getEventSetProcessorDefaultSynchronous();

    /**
     * Returns the executor shutdown timeout (in milliseconds).
     *
     * @return the executor shutdown timeout.
     */
    long getExecutorShutdownTimeout();

    /**
     * Returns the specific implementation parameter - an asynchronous event set processor processing partition size.
     *
     * @return the partition size.
     */
    int getAsyncEventSetProcessorProcessingPartitionSize();

    /**
     * Returns the specific implementation parameter - an asynchronous event set processor processing threshold.
     *
     * @return the threshold value.
     */
    int getAsyncEventSetProcessorProcessingThreshold();

    /**
     * Returns the flag to allow for concurrent processing of events that have the same type by event set processors.
     *
     * @return the flag to allow for concurrent processing of events that have the same type by event set processors.
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
     * Sets the number of threads used by processing units to listen to an event queue concurrently. In the current implementation this
     * parameter is not used.
     *
     * @param processingUnitConcurrentListenerThreadCount the number of threads used by processing units to listen to an event queue
     *        concurrently.
     */
    void setProcessingUnitConcurrentListenerThreadCount(int processingUnitConcurrentListenerThreadCount);

    /**
     * Sets the event queue capacity.
     *
     * @param eventQueueCapacity the event queue capacity.
     */
    void setEventQueueCapacity(int eventQueueCapacity);

    /**
     * Sets the duration executor thread count.
     *
     * @param durationThreadCount the duration executor thread count.
     */
    void setDurationThreadCount(int durationThreadCount);

    /**
     * Sets the number of threads used by an event set processor asynchronous executor.
     *
     * @param asyncEventSetProcessorExecutorThreadCount the number of threads used by an event set processor asynchronous executor.
     */
    void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount);

    /**
     * Sets the event clone policy.
     *
     * @param eventClonePolicy the event clone policy.
     */
    void setEventClonePolicy(EventClonePolicy eventClonePolicy);

    /**
     * Sets the event set processor default synchronous flag.
     *
     * @param eventSetProcessorDefaultSynchronous the event set processor default synchronous flag.
     */
    void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous);

    /**
     * Sets the executor shutdown timeout (in milliseconds).
     *
     * @param executorShutdownTimeout the executor shutdown timeout.
     */
    void setExecutorShutdownTimeout(long executorShutdownTimeout);

    /**
     * Sets the specific {link org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler} implementation parameter -
     * an asynchronous processing partition size.
     *
     * @param asyncEventSetProcessorProcessingPartitionSize the partition size.
     */
    void setAsyncEventSetProcessorProcessingPartitionSize(int asyncEventSetProcessorProcessingPartitionSize);

    void setAsyncEventSetProcessorProcessingThreshold(int asyncEventSetProcessorProcessingThreshold);

    /**
     * Sets the flag to allow for concurrent processing of events that have the same type by event set processors.
     *
     * @param allowConcurrentEventTypeProcessingByEventSetProcessors the flag to allow for concurrent processing of events that have the
     *        same type by event set processors.
     */
    void setAllowConcurrentEventTypeProcessingByEventSetProcessors(boolean allowConcurrentEventTypeProcessingByEventSetProcessors);

    /**
     * Sets the auto-enable of processors.
     *
     * @param autoEnable the auto-enable of processors.
     */
    void setAutoEnable(boolean autoEnable);

    /**
     * Returns the Main Processing Unit worker executor queue size.
     *
     * @return the Main Processing Unit worker executor queue size.
     */
    int getMainProcessingUnitWorkerExecutorQueueSize();

    /**
     * Sets the Main Processing Unit worker executor queue size.
     *
     * @param mainProcessingUnitWorkerExecutorQueueSize the Main Processing Unit worker executor queue size.
     */
    void setMainProcessingUnitWorkerExecutorQueueSize(int mainProcessingUnitWorkerExecutorQueueSize);

    /**
     * Returns the internal queue blocking put operation sleep time between retries (in milliseconds).
     *
     * @return the internal queue blocking put operation sleep time between retries.
     */
    long getInternalQueueBlockingPutSleep();

    /**
     * Sets the internal queue blocking put operation sleep time between retries (in milliseconds).
     *
     * @param internalQueueBlockingPutSleep the internal queue blocking put operation sleep time between retries.
     */
    void setInternalQueueBlockingPutSleep(long internalQueueBlockingPutSleep);

    /**
     * Returns the decomposed queue capacity.
     *
     * @return the decomposed queue capacity.
     */
    int getDecomposedQueueCapacity();

    /**
     * Sets the decomposed queue capacity.
     *
     * @param decomposedQueueCapacity the decomposed queue capacity.
     */
    void setDecomposedQueueCapacity(int decomposedQueueCapacity);

    /**
     * Returns the Main Event Queue capacity.
     *
     * @return the Main Event Queue capacity.
     */
    int getMainEventQueueCapacity();

    /**
     * Sets the Main Event Queue capacity.
     *
     * @param mainEventQueueCapacity the Main Event Queue capacity.
     */
    void setMainEventQueueCapacity(int mainEventQueueCapacity);

    /**
     * Returns the dynamic thread pool initial size ratio.
     *
     * @return the dynamic thread pool initial size ratio.
     */
    double getInitialDynamicThreadPoolSizeRatio();

    /**
     * Sets the dynamic thread pool initial size ratio.
     *
     * @param initialDynamicThreadPoolSizeRatio the dynamic thread pool initial size ratio.
     */
    void setInitialDynamicThreadPoolSizeRatio(double initialDynamicThreadPoolSizeRatio);

    /**
     * Returns the dynamic thread pool keep alive time (in milliseconds).
     *
     * @return the dynamic thread pool keep alive time.
     */
    long getDynamicThreadPoolKeepAliveTime();

    /**
     * Sets the dynamic thread pool keep alive time (in milliseconds).
     *
     * @param dynamicThreadPoolKeepAliveTime the dynamic thread pool keep alive time.
     */
    void setDynamicThreadPoolKeepAliveTime(long dynamicThreadPoolKeepAliveTime);

    /**
     * Returns the Processing Unit event processor cache expire time (in milliseconds).
     *
     * @return the Processing Unit event processor cache expire time (in milliseconds).
     */
    long getProcessingUnitEventProcessorCacheExpireTime();

    /**
     * Sets the Processing Unit event processor cache expire time (in milliseconds).
     *
     * @param processingUnitEventProcessorCacheExpireTime the Processing Unit event processor cache expire time (in milliseconds).
     */
    void setProcessingUnitEventProcessorCacheExpireTime(long processingUnitEventProcessorCacheExpireTime);
}
