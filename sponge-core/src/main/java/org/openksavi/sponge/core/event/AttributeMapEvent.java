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

package org.openksavi.sponge.core.event;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * An event. Event has the following standard attributes name, id, time, priority.
 *
 * <p> Note: this class has a natural ordering that is inconsistent with equals. </p>
 */
public class AttributeMapEvent extends BaseEvent {

    private static final long serialVersionUID = -34279287157404032L;

    /** Event attribute map (attribute name, attribute value). */
    private Map<String, Object> attributes = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Creates a new event that has no ID and time set (both are 0).
     *
     * @param name an event name.
     * @param clonePolicy an event clone policy.
     */
    public AttributeMapEvent(String name, EventClonePolicy clonePolicy) {
        super(name, clonePolicy);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doGet(String name, boolean useDefault, T defaultValue) {
        synchronized (attributes) {
            if (!has(name)) {
                return getDefaultAttributeValue(name, useDefault, defaultValue);
            }

            return (T) attributes.get(name);
        }
    }

    /**
     * Sets attribute value.
     *
     * @param name attribute name.
     * @param value attribute value.
     * @return this event for fluent API.
     */
    @Override
    public Event set(String name, Object value) {
        validateAttribute(name, value);

        attributes.put(name, value);

        return this;
    }

    /**
     * Checks whether this event has an attribute with the specified name.
     *
     * @param name attribute name.
     * @return {@code true} if there is such attribute.
     */
    @Override
    public boolean has(String name) {
        return attributes.containsKey(name);
    }

    /**
     * Returns attribute map.
     *
     * @return attribute map.
     */
    @Override
    public Map<String, Object> getAll() {
        return attributes;
    }

    /**
     * Clones this event.
     *
     * @return cloned event.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AttributeMapEvent clone() {
        AttributeMapEvent event = (AttributeMapEvent) super.clone();
        if (attributes != null) {
            switch (clonePolicy) {
            case SHALLOW:
                event.attributes = Collections.synchronizedMap(new LinkedHashMap<>(attributes));
                break;
            case DEEP:
                event.attributes = (Map<String, Object>) SpongeUtils.deepClone((Serializable) attributes);
                break;
            default:
                throw new SpongeException("Unsupported value: " + clonePolicy);
            }
        }

        return event;
    }
}
