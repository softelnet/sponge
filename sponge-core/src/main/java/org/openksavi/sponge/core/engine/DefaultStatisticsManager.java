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

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Statistics Manager.
 */
public class DefaultStatisticsManager extends BaseEngineModule implements StatisticsManager {

    /**
     * Creates a new Statistics Manager.
     *
     * @param engine the engine.
     */
    public DefaultStatisticsManager(Engine engine) {
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
        return queue.getName() + "; capacity=" + queue.getCapacity() + "; size=" + queue.getSize();
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
        sb.append("\n");
        sb.append("Actions: " + getEngine().getActions().size());
        sb.append(". Filters: " + getEngine().getFilters().size());
        sb.append(". Triggers: " + getEngine().getTriggers().size());
        sb.append(". Rules: " + getEngine().getRuleGroups().size());
        sb.append(". Correlators: " + getEngine().getCorrelatorGroups().size());
        sb.append("\n");
        sb.append("Event scheduler: scheduled=" + getScheduledEventCount());
        sb.append(". Threads: active threads=" + getActiveThreadCount());
        sb.append(". Plugins: plugins=" + getPluginCount());

        return sb.toString();
    }
}
