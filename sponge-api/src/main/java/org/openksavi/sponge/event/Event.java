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

import org.openksavi.sponge.util.Descriptive;
import org.openksavi.sponge.util.HasPriority;

/**
 * An event. Event has the following standard attributes: name (type), id, time, priority, label and description.
 */
public interface Event extends Cloneable, Serializable, HasPriority<Event>, Descriptive {

    /**
     * Returns the event name.
     *
     * @return the event name.
     */
    @Override
    String getName();

    /**
     * Returns the event time.
     *
     * @return the event time.
     */
    Instant getTime();

    /**
     * Sets the event time.
     *
     * @param time the event time.
     */
    void setTime(Instant time);

    /**
     * Returns the event ID.
     *
     * @return the event ID.
     */
    String getId();

    /**
     * Sets the event ID.
     *
     * @param id the event ID.
     */
    void setId(String id);

    /**
     * Returns the attribute value or throws {@code IllegalArgumentException} if it does't exist.
     *
     * @param name the attribute name.
     * @return the attribute value.
     * @param <T> attribute.
     */
    <T> T get(String name);

    /**
     * Returns the attribute value or throws {@code IllegalArgumentException} if it does't exist.
     *
     * @param cls the attribute class.
     * @param name the attribute name.
     * @return the attribute value.
     * @param <T> attribute.
     */
    <T> T get(Class<T> cls, String name);

    /**
     * Returns the attribute value or {@code defaultValue} if it does't exist.
     *
     * @param name the attribute name.
     * @param defaultValue the default value.
     * @return the attribute value.
     * @param <T> attribute.
     */
    <T> T get(String name, T defaultValue);

    /**
     * Sets attribute value.
     *
     * @param name the attribute name.
     * @param value the attribute value.
     * @return this event for fluent API.
     */
    Event set(String name, Object value);

    /**
     * Sets attributes.
     *
     * @param attributes the attributes as the map.
     * @return this event for fluent API.
     */
    Event set(Map<String, Object> attributes);

    /**
     * Checks whether this event has an attribute with the specified name.
     *
     * @param name the attribute name.
     * @return {@code true} if there is such attribute.
     */
    boolean has(String name);

    /**
     * Returns the attribute map.
     *
     * @return the attribute map.
     */
    Map<String, Object> getAll();

    /**
     * Compares events by ID.
     *
     * @param event the compared event.
     * @return {@code true} if both events are equal by ID.
     */
    boolean equalsById(Event event);

    /**
     * Compares events by attributes.
     *
     * @param event the compared event.
     * @return {@code true} if both events are equal by attributes.
     */
    boolean equalsByAttributes(Event event);

    /**
     * Clones this event.
     *
     * @return a cloned event.
     */
    Event clone();
}
