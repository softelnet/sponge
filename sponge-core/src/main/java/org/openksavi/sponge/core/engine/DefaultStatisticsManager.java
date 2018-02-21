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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.core.engine.processing.decomposed.DecomposedQueue;
import org.openksavi.sponge.core.engine.processing.decomposed.DecomposedQueueMainProcessingUnit;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;

/**
 * Statistics Manager.
 */
public class DefaultStatisticsManager extends BaseEngineModule implements StatisticsManager {

    private AtomicReference<Instant> timeMeasurementStart = new AtomicReference<>(null);

    private AtomicLong timeMeasurementEventCount = new AtomicLong(0);

    /**
     * Creates a new Statistics Manager.
     *
     * @param engine the engine.
     */
    public DefaultStatisticsManager(SpongeEngine engine) {
        super("StatisticsManager", engine);
    }

    /**
     * Returns scheduled event count.
     *
     * @return scheduled event count.
     */
    @Override
    public int getScheduledEventCount() {
        return getEngine().getEventScheduler().getEntries().size();
    }

    /**
     * Returns active thread count.
     *
     * @return active thread count.
     */
    @Override
    public int getActiveThreadCount() {
        return Thread.activeCount();
    }

    /**
     * Returns the number of plugins.
     *
     * @return the number of plugins.
     */
    @Override
    public int getPluginCount() {
        return getEngine().getPluginManager().getPlugins().size();
    }

    private String getQueueSummary(EventQueue queue) {
        return queue.getName() + " (capacity=" + queue.getCapacity() + "; size=" + queue.getSize() + ")";
    }

    @SuppressWarnings("rawtypes")
    protected String getEventSetProcessorsSummary(String label, List<? extends EventSetProcessorAdapterGroup> groups) {
        return label + "(" + groups.size() + ")" + (groups.isEmpty() ? "" : ": ")
                + groups.stream().map(group -> String.format("%s (%s)", group.getName(), group.getEventSetProcessorAdapters().size()))
                        .collect(Collectors.joining(", "));
    }

    protected String getThreadPoolSummary(ThreadPool threadPool) {
        StringBuffer sb = new StringBuffer(512);
        sb.append(threadPool.getName());

        if (threadPool.getExecutor() instanceof ThreadPoolExecutor) {
            sb.append(" (");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) threadPool.getExecutor();
            sb.append("max=" + executor.getMaximumPoolSize());
            sb.append(", current=" + executor.getPoolSize());
            sb.append(", active=" + executor.getActiveCount());
            sb.append(", largest=" + executor.getLargestPoolSize());
            sb.append(", core=" + executor.getCorePoolSize());
            sb.append(", all tasks=" + executor.getTaskCount());
            sb.append(", completed tasks=" + executor.getCompletedTaskCount());
            sb.append(", queue size=" + executor.getQueue().size());
            sb.append(", queue remaining capacity=" + executor.getQueue().remainingCapacity());
            sb.append(")");
        }

        return sb.toString();
    }

    @Override
    public Double getEventPerformance() {
        Instant start = timeMeasurementStart.get();
        long events = timeMeasurementEventCount.get();

        if (start == null || events == 0) {
            return null;
        }

        return (((double) events) / ((double) Duration.between(start, Instant.now()).toMillis())) * 1000;
    }

    public String getMemorySummary() {
        return String.format("Memory (total=%s, used=%s)", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()),
                FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    }

    @SuppressWarnings("rawtypes")
    public String getDecomposedQueueSummary(DecomposedQueueMainProcessingUnit mainProcessingUnit) {
        DecomposedQueue queue = ((DecomposedQueueMainProcessingUnit) mainProcessingUnit).getDecomposedQueue();
        return String.format("Decomposed queue (capacity=%s, size=%s)", queue.getCapacity(), queue.getSize());
    }

    /**
     * Returns statistics summary.
     *
     * @return statistics summary.
     */
    @Override
    public String getSummary() {
        StringBuffer sb = new StringBuffer(512);

        sb.append(getQueueSummary(getEngine().getEventQueueManager().getInputEventQueue()));
        sb.append(". " + getQueueSummary(getEngine().getEventQueueManager().getMainEventQueue()));
        sb.append(". Plugins (" + getPluginCount() + ")");
        sb.append(". Actions (" + getEngine().getActions().size() + ")");
        sb.append(". Filters (" + getEngine().getFilters().size() + ")");
        sb.append(". Triggers (" + getEngine().getTriggers().size() + ")");
        sb.append(". " + getEventSetProcessorsSummary("Rules", getEngine().getRuleGroups()));
        sb.append(". " + getEventSetProcessorsSummary("Correlators", getEngine().getCorrelatorGroups()));
        sb.append(". Event scheduler (" + getScheduledEventCount() + ")");
        sb.append(". Thread pools: ");
        MainProcessingUnit mainProcessingUnit = getEngine().getProcessingUnitManager().getMainProcessingUnit();
        sb.append(getThreadPoolSummary(mainProcessingUnit.getWorkerThreadPool()));
        sb.append(", " + getThreadPoolSummary(mainProcessingUnit.getAsyncEventSetProcessorThreadPool()));
        if (mainProcessingUnit instanceof DecomposedQueueMainProcessingUnit) {
            sb.append(". " + getDecomposedQueueSummary((DecomposedQueueMainProcessingUnit) mainProcessingUnit));
        }
        sb.append(". " + getMemorySummary());
        Double eventPerformance = getEventPerformance();
        if (eventPerformance != null) {
            sb.append(String.format(". Events performance (%.2f events/s)", eventPerformance));
        }

        sb.append(".");

        return sb.toString();
    }

    @Override
    public void startTimeMeasurementIfNotStartedYet() {
        timeMeasurementStart.compareAndSet(null, Instant.now());
    }

    @Override
    public void incrementTimeMeasurementEventCount() {
        timeMeasurementEventCount.incrementAndGet();
    }

    @Override
    public void clearTimeMeasurement() {
        timeMeasurementStart.set(null);
        timeMeasurementEventCount.set(0);
    }
}
