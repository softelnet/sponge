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

package org.openksavi.sponge;

import org.openksavi.sponge.event.Event;

/**
 * Event set processor adapter.
 */
public interface EventSetProcessorAdapter<T extends EventSetProcessor<?>> extends EventProcessorAdapter<T>, EventSetProcessorOperations {

    /**
     * Sets the state.
     *
     * @param state the state.
     */
    void setState(EventSetProcessorState state);

    /**
     * Returns the state.
     *
     * @return the state.
     */
    EventSetProcessorState getState();

    /**
     * Informs whether this event set processor is running.
     *
     * @return whether this event set processor is running.
     */
    boolean isRunning();

    /**
     * Finishes this event set processor.
     */
    void finish();

    /**
     * Invoked when event set processor duration occurs.
     */
    void durationOccurred();

    /**
     * Processes an event.
     *
     * @param event event.
     */
    void processEvent(Event event);

    /**
     * Sets event set processor group.
     *
     * @param group event set processor group.
     */
    void setGroup(EventSetProcessorAdapterGroup<? extends EventSetProcessorAdapter<?>> group);

    /**
     * Returns event set processor group.
     *
     * @return event set processor group.
     */
    EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>> getGroup();

    /**
     * Checks if the event should be accepted as the first event of this event set processor, therefore starting the new instance.
     *
     * @param event the incoming event.
     * @return if the event should be accepted as the first event.
     */
    boolean acceptsAsFirst(Event event);
}
