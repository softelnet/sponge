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

package org.openksavi.sponge;

import java.util.List;

import org.openksavi.sponge.event.Event;

/**
 * Event set processor adapter group.
 */
@SuppressWarnings("rawtypes")
public interface EventSetProcessorAdapterGroup<T extends EventSetProcessorAdapter> extends EventProcessorAdapter {

    /**
     * Returns all event set processor adapters that belong to this group.
     *
     * @return all event set processor adapters that belong to this group.
     */
    List<T> getEventSetProcessorAdapters();

    /**
     * Processes the specified event.
     *
     * @param event event.
     */
    void processEvent(Event event);

    /**
     * Invoked when event set processor adapter duration occurs.
     *
     * @param adapter adapter.
     */
    void durationOccurred(T adapter);

    /**
     * Removes the event set processor duration task.
     *
     * @param adapter the event set processor adapter.
     */
    void removeDuration(T adapter);
}
