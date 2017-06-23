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
     * Returns the maximum event queue size.
     *
     * @return the maximum event queue size.
     */
    int getEventQueueMaxSize();

    /**
     * Returns the number of threads used by cron.
     *
     * @return the number of threads used by cron.
     */
    int getCronThreadCount();

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
     * @param mainProcessingUnitThreadCount
     *            the number of the Main Processing Unit worker threads.
     */
    void setMainProcessingUnitThreadCount(int mainProcessingUnitThreadCount);

    /**
     * Sets the number of threads used by processing units to listen to an event queue concurrently.
     *
     * @param processingUnitConcurrentListenerThreadCount
     *            the number of threads used by processing units to listen to an event queue concurrently.
     */
    void setProcessingUnitConcurrentListenerThreadCount(int processingUnitConcurrentListenerThreadCount);

    /**
     * Sets event queue max size.
     *
     * @param eventQueueMaxSize
     *            event queue max size.
     */
    void setEventQueueMaxSize(int eventQueueMaxSize);

    /**
     * Sets cron thread count.
     *
     * @param cronThreadCount
     *            cron thread count.
     */
    void setCronThreadCount(int cronThreadCount);

    /**
     * Sets duration executor thread count.
     *
     * @param durationThreadCount
     *            duration executor thread count.
     */
    void setDurationThreadCount(int durationThreadCount);

    /**
     * Sets the number of threads used by an event set processor asynchronous executor.
     *
     * @param asyncEventSetProcessorExecutorThreadCount
     *            the number of threads used by an event set processor asynchronous executor.
     */
    void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount);

    /**
     * Sets event clone policy.
     *
     * @param eventClonePolicy
     *            event clone policy.
     */
    void setEventClonePolicy(EventClonePolicy eventClonePolicy);

    /**
     * Sets an event set processor default synchronous flag.
     *
     * @param eventSetProcessorDefaultSynchronous
     *            an event set processor default synchronous flag.
     */
    void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous);

    /**
     * Sets an Executor shutdown timeout.
     *
     * @param executorShutdownTimeout
     *            an Executor shutdown timeout.
     */
    void setExecutorShutdownTimeout(long executorShutdownTimeout);

    /**
     * Sets a specific {link org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler} implementation
     * parameter - an asynchronous processing partition size.
     *
     * @param asyncEventSetProcessorProcessingPartitionSize
     *            a partition size.
     */
    void setAsyncEventSetProcessorProcessingPartitionSize(int asyncEventSetProcessorProcessingPartitionSize);

    void setAsyncEventSetProcessorProcessingThreshold(int asyncEventSetProcessorProcessingThreshold);

    /**
     * Sets a flag to allow for concurrent processing of events that have the same type by event set processors.
     *
     * @param allowConcurrentEventTypeProcessingByEventSetProcessors
     *            a flag to allow for concurrent processing of events that have the same type by event set processors.
     */
    void setAllowConcurrentEventTypeProcessingByEventSetProcessors(boolean allowConcurrentEventTypeProcessingByEventSetProcessors);

    /**
     * Sets auto-enable of processors.
     *
     * @param autoEnable
     *            auto-enable of processors.
     */
    void setAutoEnable(boolean autoEnable);
}
