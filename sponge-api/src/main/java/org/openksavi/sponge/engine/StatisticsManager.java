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

/**
 * Statistics Manager.
 */
public interface StatisticsManager extends EngineModule {

    /**
     * Returns scheduled event count.
     *
     * @return scheduled event count.
     */
    int getScheduledEventCount();

    /**
     * Returns active thread count.
     *
     * @return active thread count.
     */
    int getActiveThreadCount();

    /**
     * Returns the number of plugins.
     *
     * @return the number of plugins.
     */
    int getPluginCount();

    /**
     * Returns statistics summary.
     *
     * @return statistics summary.
     */
    String getSummary();

    /**
     * Starts the time measurement of events to be processed by the Main Processing Unit.
     */
    void startTimeMeasurementIfNotStartedYet();

    /**
     * Increments event count in the time measurement of events to be processed by the Main Processing Unit.
     */
    void incrementTimeMeasurementEventCount();

    /**
     * Returns the event performance calculated in the current time measurement. Returns {@code null} if there is no data available.
     * 
     * @return the event performance as events / second.
     */
    Double getEventPerformance();

    /**
     * Clears the time measurement of events to be processed by the Main Processing Unit.
     */
    void clearTimeMeasurement();
}
