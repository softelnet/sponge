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

import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A control event that may have a priority other than default.
 */
public abstract class BaseControlEvent extends BaseEvent implements ControlEvent {

    private static final long serialVersionUID = 3385700335341046972L;

    /** Default control event priority. Equals to {@code 10}. */
    public static final int DEFAULT_CONTROL_EVENT_PRIORITY = 10;

    /** Event priority. */
    private int priority = DEFAULT_CONTROL_EVENT_PRIORITY;

    protected BaseControlEvent(String name, EventClonePolicy clonePolicy) {
        super(name, clonePolicy);
    }

    protected BaseControlEvent(String name) {
        this(name, EventClonePolicy.SHALLOW);
    }

    /**
     * Returns event priority.
     *
     * @return event priority.
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Sets event priority.
     *
     * @param priority event priority.
     * @return this event for fluent API.
     */
    @Override
    public BaseControlEvent setPriority(int priority) {
        this.priority = priority;
        return this;
    }
}
