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

package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.engine.EngineParameters;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Default values of engine parameters.
 */
public class DefaultEngineParameters implements EngineParameters {

    /** The default number of the Main Processing Unit worker threads. */
    private int mainProcessingUnitThreadCount = 10;

    /** The default number of threads used by an event set processor asynchronous executor. */
    private int asyncEventSetProcessorExecutorThreadCount = 10;

    /**
     * The default number of threads used by processing units to listen to an event queue concurrently. In the current implementation this
     * parameter is not used.
     */
    private int processingUnitConcurrentListenerThreadCount = 1;

    /** The default event queue capacity. */
    private int eventQueueCapacity = -1;

    /** The default duration thread count. */
    private int durationThreadCount = 2;

    /** The default event clone policy. */
    private EventClonePolicy eventClonePolicy = EventClonePolicy.SHALLOW;

    /** The default event set processor synchronous flag. */
    private boolean eventSetProcessorDefaultSynchronous = false;

    /** The default value of auto-enable processors. */
    private boolean autoEnable = true;

    /** The executor shutdown timeout (in milliseconds). */
    private long executorShutdownTimeout = 60000;

    /**
     * The specific {link org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler} implementation parameter - the
     * asynchronous processing partition size.
     */
    private int asyncEventSetProcessorProcessingPartitionSize = 1000;

    private int asyncEventSetProcessorProcessingThreshold = 1;

    /** The flag to allow for concurrent processing of events that have the same type by event set processors. */
    private boolean allowConcurrentEventTypeProcessingByEventSetProcessors = true;

    /** The Main Processing Unit worker executor queue size. */
    private int mainProcessingUnitWorkerExecutorQueueSize = 100;

    /** The internal queue blocking put operation sleep time between retries (in milliseconds). */
    private long internalQueueBlockingPutSleep = 10;

    /** The decomposed queue capacity. */
    private int decomposedQueueCapacity = 100;

    /** The Main Event Queue capacity. */
    private int mainEventQueueCapacity = 10;

    /** The dynamic thread pool initial size ratio. */
    private double initialDynamicThreadPoolSizeRatio = 0.3;

    /** The dynamic thread pool keep alive time (in milliseconds). */
    private long dynamicThreadPoolKeepAliveTime = 60000;

    /**
     * The Processing Unit event processor cache expire time (in milliseconds). The value {@code -1} means that the cache is turned off. The
     * value {@code 0} means that there will be no expire time for this cache. The default value is {@code 0}.
     */
    private long processingUnitEventProcessorCacheExpireTime = 0;

    /**
     * The script-based class instance creation expression cache expire time (in milliseconds). The value {@code -1} means that the cache is
     * turned off. The value {@code 0} means that there will be no expire time for this cache. The default value is {@code 0}.
     */
    private long scriptClassInstancePoviderCacheExpireTime = 0;

    @Override
    public int getMainProcessingUnitThreadCount() {
        return mainProcessingUnitThreadCount;
    }

    @Override
    public int getProcessingUnitConcurrentListenerThreadCount() {
        return processingUnitConcurrentListenerThreadCount;
    }

    @Override
    public int getEventQueueCapacity() {
        return eventQueueCapacity;
    }

    @Override
    public int getDurationThreadCount() {
        return durationThreadCount;
    }

    @Override
    public int getAsyncEventSetProcessorExecutorThreadCount() {
        return asyncEventSetProcessorExecutorThreadCount;
    }

    @Override
    public EventClonePolicy getEventClonePolicy() {
        return eventClonePolicy;
    }

    @Override
    public boolean getEventSetProcessorDefaultSynchronous() {
        return eventSetProcessorDefaultSynchronous;
    }

    @Override
    public long getExecutorShutdownTimeout() {
        return executorShutdownTimeout;
    }

    @Override
    public boolean getAllowConcurrentEventTypeProcessingByEventSetProcessors() {
        return allowConcurrentEventTypeProcessingByEventSetProcessors;
    }

    @Override
    public int getAsyncEventSetProcessorProcessingPartitionSize() {
        return asyncEventSetProcessorProcessingPartitionSize;
    }

    @Override
    public boolean getAutoEnable() {
        return autoEnable;
    }

    @Override
    public void setMainProcessingUnitThreadCount(int mainProcessingUnitThreadCount) {
        this.mainProcessingUnitThreadCount = mainProcessingUnitThreadCount;
    }

    @Override
    public void setProcessingUnitConcurrentListenerThreadCount(int processingUnitConcurrentListenerThreadCount) {
        this.processingUnitConcurrentListenerThreadCount = processingUnitConcurrentListenerThreadCount;
    }

    @Override
    public void setEventQueueCapacity(int eventQueueCapacity) {
        this.eventQueueCapacity = eventQueueCapacity;
    }

    @Override
    public void setDurationThreadCount(int durationThreadCount) {
        this.durationThreadCount = durationThreadCount;
    }

    @Override
    public void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount) {
        this.asyncEventSetProcessorExecutorThreadCount = asyncEventSetProcessorExecutorThreadCount;
    }

    @Override
    public void setEventClonePolicy(EventClonePolicy eventClonePolicy) {
        this.eventClonePolicy = eventClonePolicy;
    }

    @Override
    public void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous) {
        this.eventSetProcessorDefaultSynchronous = eventSetProcessorDefaultSynchronous;
    }

    @Override
    public void setExecutorShutdownTimeout(long executorShutdownTimeout) {
        this.executorShutdownTimeout = executorShutdownTimeout;
    }

    @Override
    public void setAsyncEventSetProcessorProcessingPartitionSize(int asyncEventSetProcessorProcessingPartitionSize) {
        this.asyncEventSetProcessorProcessingPartitionSize = asyncEventSetProcessorProcessingPartitionSize;
    }

    @Override
    public void setAllowConcurrentEventTypeProcessingByEventSetProcessors(boolean allowConcurrentEventTypeProcessingByEventSetProcessors) {
        this.allowConcurrentEventTypeProcessingByEventSetProcessors = allowConcurrentEventTypeProcessingByEventSetProcessors;
    }

    @Override
    public void setAutoEnable(boolean autoEnable) {
        this.autoEnable = autoEnable;
    }

    @Override
    public int getAsyncEventSetProcessorProcessingThreshold() {
        return asyncEventSetProcessorProcessingThreshold;
    }

    @Override
    public void setAsyncEventSetProcessorProcessingThreshold(int asyncEventSetProcessorProcessingThreshold) {
        this.asyncEventSetProcessorProcessingThreshold = asyncEventSetProcessorProcessingThreshold;
    }

    @Override
    public int getMainProcessingUnitWorkerExecutorQueueSize() {
        return mainProcessingUnitWorkerExecutorQueueSize;
    }

    @Override
    public void setMainProcessingUnitWorkerExecutorQueueSize(int mainProcessingUnitWorkerExecutorQueueSize) {
        this.mainProcessingUnitWorkerExecutorQueueSize = mainProcessingUnitWorkerExecutorQueueSize;
    }

    @Override
    public long getInternalQueueBlockingPutSleep() {
        return internalQueueBlockingPutSleep;
    }

    @Override
    public void setInternalQueueBlockingPutSleep(long internalQueueBlockingPutSleep) {
        this.internalQueueBlockingPutSleep = internalQueueBlockingPutSleep;
    }

    @Override
    public int getDecomposedQueueCapacity() {
        return decomposedQueueCapacity;
    }

    @Override
    public void setDecomposedQueueCapacity(int decomposedQueueCapacity) {
        this.decomposedQueueCapacity = decomposedQueueCapacity;
    }

    @Override
    public int getMainEventQueueCapacity() {
        return mainEventQueueCapacity;
    }

    @Override
    public void setMainEventQueueCapacity(int mainEventQueueCapacity) {
        this.mainEventQueueCapacity = mainEventQueueCapacity;
    }

    @Override
    public double getInitialDynamicThreadPoolSizeRatio() {
        return initialDynamicThreadPoolSizeRatio;
    }

    @Override
    public void setInitialDynamicThreadPoolSizeRatio(double initialDynamicThreadPoolSizeRatio) {
        this.initialDynamicThreadPoolSizeRatio = initialDynamicThreadPoolSizeRatio;
    }

    @Override
    public long getDynamicThreadPoolKeepAliveTime() {
        return dynamicThreadPoolKeepAliveTime;
    }

    @Override
    public void setDynamicThreadPoolKeepAliveTime(long dynamicThreadPoolKeepAliveTime) {
        this.dynamicThreadPoolKeepAliveTime = dynamicThreadPoolKeepAliveTime;
    }

    @Override
    public long getProcessingUnitEventProcessorCacheExpireTime() {
        return processingUnitEventProcessorCacheExpireTime;
    }

    @Override
    public void setProcessingUnitEventProcessorCacheExpireTime(long processingUnitEventProcessorCacheExpireTime) {
        this.processingUnitEventProcessorCacheExpireTime = processingUnitEventProcessorCacheExpireTime;
    }

    @Override
    public long getScriptClassInstancePoviderCacheExpireTime() {
        return scriptClassInstancePoviderCacheExpireTime;
    }

    @Override
    public void setScriptClassInstancePoviderCacheExpireTime(long scriptClassInstancePoviderCacheExpireTime) {
        this.scriptClassInstancePoviderCacheExpireTime = scriptClassInstancePoviderCacheExpireTime;
    }
}
