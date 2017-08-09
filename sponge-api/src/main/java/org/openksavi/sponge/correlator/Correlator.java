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

package org.openksavi.sponge.correlator;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.event.Event;

/**
 * Correlator.
 */
public interface Correlator extends EventSetProcessor<CorrelatorAdapter>, CorrelatorOperations {

    /**
     * A callback method that checks if this event should be accepted as the first event of this correlator, therefore starting a new
     * instance.
     *
     * @param event the incoming event.
     * @return {@code true} if this event should be accepted as the first event.
     */
    boolean onAcceptAsFirst(Event event);

    /**
     * Callback invoked when an event happens.
     *
     * @param event event.
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
