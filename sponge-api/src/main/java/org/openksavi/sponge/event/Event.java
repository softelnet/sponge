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

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

import org.openksavi.sponge.util.HasPriority;

/**
 * An event. Event has the following standard attributes: name (type), id, time, priority.
 */
public interface Event extends Cloneable, Serializable, HasPriority<Event> {

    /**
     * Returns event name.
     *
     * @return event name.
     */
    String getName();

    /**
     * Returns event time.
     *
     * @return event time.
     */
    Instant getTime();

    /**
     * Sets event time.
     *
     * @param time event time.
     */
    void setTime(Instant time);

    /**
     * Returns event ID.
     *
     * @return event ID.
     */
    String getId();

    /**
     * Sets event ID.
     *
     * @param id event ID.
     */
    void setId(String id);

    /**
     * Returns the attribute value or {@code null} if it does't exist.
     *
     * @param name attribute name.
     * @return attribute value.
     * @param <T> attribute.
     */
    <T> T get(String name);

    /**
     * Returns the attribute value or {@code null} if it does't exist.
     *
     * @param name attribute name.
     * @param cls attribute class.
     * @return attribute value.
     * @param <T> attribute.
     */
    <T> T get(String name, Class<T> cls);

    /**
     * Sets attribute value.
     *
     * @param name attribute name.
     * @param value attribute value.
     * @return this event for fluent API.
     */
    Event set(String name, Object value);

    /**
     * Checks whether this event has an attribute with the specified name.
     *
     * @param name attribute name.
     * @return {@code true} if there is such attribute.
     */
    boolean has(String name);

    /**
     * Returns attribute map.
     *
     * @return attribute map.
     */
    Map<String, Object> getAll();

    /**
     * Compares events by ID.
     *
     * @param event compared event.
     * @return {@code true} if both events are equal by ID.
     */
    boolean equalsById(Event event);

    /**
     * Compares events by attributes.
     *
     * @param event compared event.
     * @return {@code true} if both events are equal by attributes.
     */
    boolean equalsByAttributes(Event event);

    /**
     * Clones this event. Attributes are deeply cloned.
     *
     * @return cloned event.
     */
    Event clone();
}
