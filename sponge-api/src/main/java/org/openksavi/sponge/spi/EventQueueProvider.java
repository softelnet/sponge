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

package org.openksavi.sponge.spi;

import org.openksavi.sponge.engine.event.EventQueue;

/**
 * Event queue provider for the the engine.
 */
public interface EventQueueProvider {

    /**
     * Creates an input event queue.
     *
     * @return an input event queue.
     */
    EventQueue getInputQueue();

    /**
     * Creates a main event queue.
     *
     * @return a main event queue.
     */
    EventQueue getMainQueue();

    /**
     * Creates an output event queue.
     *
     * @return an output event queue.
     */
    EventQueue getOutputQueue();
}
