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

/**
 * Event processor operations.
 *
 */
public interface EventProcessorOperations extends ProcessorOperations {

    /**
     * Returns event names for which this processor is registered.
     *
     * @return event names.
     */
    String[] getEventNames();

    /**
     * Sets event names for which this processor is registered.
     *
     * @param eventNames
     *            event names.
     */
    void setEventNames(String... eventNames);

    /**
     * Sets event name for which this processor is registered.
     *
     * @param eventName
     *            event name.
     */
    void setEventName(String eventName);
}
