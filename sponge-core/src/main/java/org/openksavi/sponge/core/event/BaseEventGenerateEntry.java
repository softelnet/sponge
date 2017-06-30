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

package org.openksavi.sponge.core.event;

import org.openksavi.sponge.event.EventGenerateEntry;

/**
 * Entry for events to generate in the future.
 */
public class BaseEventGenerateEntry implements EventGenerateEntry {

    private static final long serialVersionUID = -3454054264269037804L;

    private Object id;

    private String eventName;

    public BaseEventGenerateEntry(Object id) {
        this.id = id;
    }

    /**
     * Returns entry ID.
     *
     * @return entry ID.
     */
    @Override
    public Object getId() {
        return id;
    }

    /**
     * Sets entry ID.
     *
     * @param id entry ID.
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * Returns event name.
     *
     * @return event name.
     */
    @Override
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets event name.
     *
     * @param eventName event name.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
