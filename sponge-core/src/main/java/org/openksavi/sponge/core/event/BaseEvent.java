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

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Base event.
 */
public abstract class BaseEvent implements Event {

    private static final long serialVersionUID = -2269323477538266057L;

    /** Default event priority. Equals to {@code 0}. */
    public static final int DEFAULT_PRIORITY = 0;

    /** Event ID. */
    protected String id;

    /** Event creation time. */
    protected Instant time;

    /** Event name. */
    protected String name;

    /** Event clone policy. */
    protected EventClonePolicy clonePolicy;

    /**
     * Creates a new event that has no ID and time set (both are 0).
     *
     * @param name
     *            an event name.
     * @param clonePolicy
     *            an event clone policy.
     */
    protected BaseEvent(String name, EventClonePolicy clonePolicy) {
        if (name == null) {
            throw new IllegalArgumentException("Event name cannot be null");
        }
        this.name = name;
        this.clonePolicy = clonePolicy;
    }

    /**
     * Returns event name.
     *
     * @return event name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns event time.
     *
     * @return event time.
     */
    @Override
    public Instant getTime() {
        return time;
    }

    /**
     * Sets event time.
     *
     * @param time
     *            event time.
     */
    @Override
    public void setTime(Instant time) {
        this.time = time;
    }

    /**
     * Returns event ID.
     *
     * @return event ID.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets event ID.
     *
     * @param id
     *            event ID.
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns event priority. This is always {@code 0} for standard events and cannot be changed.
     *
     * @return event priority.
     */
    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Returns attribute value.
     *
     * @param name
     *            attribute name.
     * @param cls
     *            attribute class.
     * @return attribute value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> cls) {
        return (T) get(name);
    }

    /**
     * Compares events by ID.
     *
     * @param event
     *            compared event.
     * @return {@code true} if both events are equal by ID.
     */
    @Override
    public boolean equalsById(Event event) {
        return event != null && Objects.equals(id, event.getId());
    }

    /**
     * Compares events by attributes.
     *
     * @param event
     *            compared event.
     * @return {@code true} if both events are equal by attributes.
     */
    @Override
    public boolean equalsByAttributes(Event event) {
        if (event == null) {
            return false;
        }

        if (event == this) {
            return true;
        }

        return Objects.equals(name, event.getName()) && getAll().equals(event.getAll());
    }

    /**
     * Matches this event to the specified template.
     *
     * @param template
     *            event template.
     * @return {@code true} if this event matches the specified
     *         template.
     */
    @Override
    public boolean matches(Event template) {
        if (template == null) {
            return false;
        }

        if (!Objects.equals(name, template.getName())) {
            return false;
        }

        Map<String, Object> attributes = getAll();
        Map<String, Object> templateAll = template.getAll();
        if (templateAll != null && !templateAll.isEmpty()) {
            for (Map.Entry<String, Object> entry : templateAll.entrySet()) {
                if (!attributes.containsKey(entry.getKey())) {
                    return false;
                }
                if (!Objects.equals(entry.getValue(), attributes.get(entry.getKey()))) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void valudateAttribute(String name, Object value) {
        if (clonePolicy == EventClonePolicy.DEEP && !(value instanceof Serializable)) {
            throw new SpongeException("Not serializable attribute can't be deep cloned.");
        }
    }

    @Override
    public BaseEvent clone() {
        try {
            return (BaseEvent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }

    /**
     * Returns event description.
     *
     * @return event description.
     */
    @Override
    public String toString() {
        return name + ", id=" + id + ", priority=" + getPriority() + ", time=" + time + ", attributes=" + getAll();
    }
}
