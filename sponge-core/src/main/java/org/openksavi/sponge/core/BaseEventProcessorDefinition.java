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

package org.openksavi.sponge.core;

import org.openksavi.sponge.EventProcessorDefinition;

public abstract class BaseEventProcessorDefinition extends BaseProcessorDefinition implements EventProcessorDefinition {

    /** Registered event names. */
    private String[] eventNames;

    /**
     * Returns event names for which this processor definition is registered.
     *
     * @return event names.
     */
    @Override
    public String[] getEventNames() {
        return eventNames;
    }

    @Override
    public String getEventName(int index) {
        return eventNames[index];
    }

    /**
     * Sets event names for which this processor definition is registered.
     *
     * @param eventNames
     *            event names.
     */
    @Override
    public void setEventNames(String... eventNames) {
        this.eventNames = eventNames;
    }

    @Override
    public void setEventName(String eventName) {
        setEventNames(eventName);
    }
}
