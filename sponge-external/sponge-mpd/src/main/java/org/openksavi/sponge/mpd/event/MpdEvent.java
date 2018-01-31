/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * This file is part of Sponge MPD Support.
 *
 * Sponge MPD Support is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sponge MPD Support is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openksavi.sponge.mpd.event;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.core.event.BaseEvent;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A MPD event wrapped in a Sponge event.
 */
public class MpdEvent<S> extends BaseEvent {

    private static final long serialVersionUID = -623018925997136503L;

    /** The source MPD event attribute name. */
    public static final String ATTR_SOURCE = "source";

    /** The source MPD event. */
    private S source;

    /**
     * Creates a new MPD Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param source the source MPD event.
     */
    public MpdEvent(String name, EventClonePolicy clonePolicy, S source) {
        super(name, clonePolicy);

        this.source = source;
    }

    /**
     * Returns the source MPD event.
     *
     * @return the source MPD event.
     */
    public S getSource() {
        return source;
    }

    /**
     * Sets the source MPD event.
     *
     * @param source the source MPD event.
     */
    public void setSource(S source) {
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T doGet(String name, boolean useDefault, T defaultValue) {
        switch (name) {
        case ATTR_SOURCE:
            return (T) source;
        default:
            return getDefaultAttributeValue(name, useDefault, defaultValue);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MpdEvent<S> set(String name, Object value) {
        switch (name) {
        case ATTR_SOURCE:
            source = (S) value;
            break;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_SOURCE.equals(name);
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(ATTR_SOURCE, source);

        return Collections.unmodifiableMap(result);
    }
}
