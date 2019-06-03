/*
 * Copyright 2016-2017 The Sponge authors.
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

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * An event definition providing a fluent API for creating and sending events.
 */
public interface EventDefinition {

    /**
     * Sets the event label.
     *
     * @param label the event label.
     * @return @return this event definition for fluent API.
     */
    EventDefinition label(String label);

    /**
     * Sets the event description.
     *
     * @param description the event description.
     * @return @return this event definition for fluent API.
     */
    EventDefinition description(String description);

    /**
     * Sets event attribute.
     *
     * @param name attribute name.
     * @param value attribute value.
     * @return this event definition for fluent API.
     */
    EventDefinition set(String name, Object value);

    /**
     * Sets attributes.
     *
     * @param attributes the attributes as the map.
     * @return this event for fluent API.
     */
    EventDefinition set(Map<String, Object> attributes);

    /**
     * Modifies the underlying event.
     *
     * @param modifier event definition modifier.
     * @return this event definition for fluent API.
     */
    EventDefinition modify(EventDefinitionModifier modifier);

    /**
     * Sends the event to the Input Event Queue. May throw {@code QueueFullException} if the Input Event Queue is full.
     *
     * @return an event.
     */
    Event send();

    /**
     * Sends the event to the Input Event Queue after a specified time.
     *
     * @param delay delay in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(long delay);

    /**
     * Sends the event to the Input Event Queue periodically after a specified time.
     *
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(long delay, long interval);

    /**
     * Sends the event to the Input Event Queue after a specified time.
     *
     * @param delay delay as Duration.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(Duration delay);

    /**
     * Sends the event to the Input Event Queue periodically after a specified time.
     *
     * @param delay delay as Duration.
     * @param interval interval as Duration.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(Duration delay, Duration interval);

    /**
     * Sends the event to the Input Event Queue at a specified time.
     *
     * @param milliseconds time in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(long milliseconds);

    /**
     * Sends the event to the Input Event Queue at a specified time and then periodically.
     *
     * @param milliseconds time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(long milliseconds, long interval);

    /**
     * Sends the event to the Input Event Queue at a specified time.
     *
     * @param instant time as Instant.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(Instant instant);

    /**
     * Sends the event to the Input Event Queue at a specified time and then periodically.
     *
     * @param instant time as Instant.
     * @param interval interval as Duration.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(Instant instant, Duration interval);

    /**
     * Sends the event to the Input Event Queue at a time specified by the crontab spec.
     *
     * @param crontabSpec crontab spec.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(String crontabSpec);

    /**
     * Sends the event to the Input Event Queue periodically.
     *
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendEvery(long interval);

    /**
     * Sends the event to the Input Event Queue periodically.
     *
     * @param interval interval as Duration.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendEvery(Duration interval);

    /**
     * Returns the event.
     *
     * @return the event.
     */
    Event make();
}
