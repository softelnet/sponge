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

package org.openksavi.sponge.aggregator;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.event.Event;

/**
 * Aggregator.
 */
public interface Aggregator extends EventSetProcessor<AggregatorAdapter> {

    /**
     * Checks if this event should be accepted as the first event of this aggregator therefore starting the new instance.
     *
     * @param event
     *            the incoming event.
     * @return if this event should be accepted as the first event.
     */
    boolean acceptsAsFirst(Event event);

    /**
     * Callback invoked when event happens.
     *
     * @param event
     *            event.
     */
    void onEvent(Event event);

    /**
     * Callback invoked when duration timeout occurs.
     */
    void onDuration();

    /**
     * Finishes this event set processor.
     */
    void finish();
}
