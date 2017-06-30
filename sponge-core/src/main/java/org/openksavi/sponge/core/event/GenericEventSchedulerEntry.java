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

import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * Entry for scheduled events.
 */
public class GenericEventSchedulerEntry extends BaseEventGenerateEntry implements EventSchedulerEntry {

    private static final long serialVersionUID = 6877340795706961804L;

    private long delay = 0;

    private long interval = 0;

    private long milliseconds = 0;

    public GenericEventSchedulerEntry(Object id) {
        super(id);
    }

    /**
     * Returns scheduled event delay (in milliseconds).
     *
     * @return scheduled event delay or {@code 0} if there is no delay (event is generated at a specified time).
     */
    @Override
    public long getDelay() {
        return delay;
    }

    /**
     * Sets event delay.
     *
     * @param delay event delay.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * Returns scheduled event interval (in milliseconds).
     *
     * @return scheduled event interval or {@code 0} if there is no interval.
     */
    @Override
    public long getInterval() {
        return interval;
    }

    /**
     * Sets event interval.
     *
     * @param interval event interval.
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * Returns scheduled event time (in milliseconds).
     *
     * @return scheduled event time or {@code 0} if there is no specific time (event is generated after delay).
     */
    @Override
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * Sets event milliseconds.
     *
     * @param milliseconds event milliseconds.
     */
    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
