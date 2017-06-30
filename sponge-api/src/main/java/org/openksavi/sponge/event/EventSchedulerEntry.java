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

package org.openksavi.sponge.event;

/**
 * Entry identifying an event scheduled in Event Scheduler.
 */
public interface EventSchedulerEntry extends EventGenerateEntry {

    /**
     * Returns scheduled event delay (in milliseconds).
     *
     * @return scheduled event delay or {@code 0} if there is no delay (event is generated at a specified time).
     */
    long getDelay();

    /**
     * Returns scheduled event interval (in milliseconds).
     *
     * @return scheduled event interval or {@code 0} if there is no interval.
     */
    long getInterval();

    /**
     * Returns scheduled event time (in milliseconds).
     *
     * @return scheduled event time or {@code 0} if there is no specific time (event is generated after delay).
     */
    long getMilliseconds();
}
