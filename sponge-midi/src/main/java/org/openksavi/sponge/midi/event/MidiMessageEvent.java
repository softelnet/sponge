/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.midi.event;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sound.midi.MidiMessage;

import org.openksavi.sponge.core.event.BaseEvent;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A MIDI Message wrapped in a Sponge event.
 */
public abstract class MidiMessageEvent<M extends MidiMessage> extends BaseEvent {

    private static final long serialVersionUID = 4119731368862161071L;

    /** The MIDI message attribute name. */
    public static final String ATTR_MESSAGE = "message";

    /** The MIDI timeStamp attribute name. */
    public static final String ATTR_TIME_STAMP = "timeStamp";

    /** The MIDI message. */
    private M message;

    /** The MIDI timeStamp. */
    private Long timeStamp;

    /**
     * A MIDI-based Sponge event category.
     */
    public static enum MidiEventCategory {
        SHORT("short"), META("meta"), SYSEX("sysex");

        private String code;

        private MidiEventCategory(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Creates a new MIDI Message Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI Message.
     * @param timeStamp the MIDI timeStamp.
     */
    public MidiMessageEvent(String name, EventClonePolicy clonePolicy, M message, Long timeStamp) {
        super(name, clonePolicy);

        this.message = message;
        this.timeStamp = timeStamp;
    }

    /**
     * Creates a new MIDI Message Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI ShortMessage.
     */
    public MidiMessageEvent(String name, EventClonePolicy clonePolicy, M message) {
        this(name, clonePolicy, message, null);
    }

    /**
     * Returns the MIDI message.
     *
     * @return the MIDI message.
     */
    public M getMessage() {
        return message;
    }

    /**
     * Sets the MIDI message.
     *
     * @param message the MIDI message.
     */
    public void setMessage(M message) {
        this.message = message;
    }

    /**
     * Returns the MIDI timeStamp.
     *
     * @return the MIDI timeStamp.
     */
    public Long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the MIDI timeStamp.
     *
     * @param timeStamp the MIDI timeStamp.
     */
    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T doGet(String name, boolean useDefault, T defaultValue) {
        Object result;
        switch (name) {
        case ATTR_MESSAGE:
            result = message;
            break;
        case ATTR_TIME_STAMP:
            result = timeStamp;
            break;
        default:
            return getDefaultAttributeValue(name, useDefault, defaultValue);
        }

        return (T) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MidiMessageEvent<M> set(String name, Object value) {
        switch (name) {
        case ATTR_MESSAGE:
            message = (M) value;
            break;
        case ATTR_TIME_STAMP:
            timeStamp = (Long) value;
            break;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_MESSAGE.equals(name) || ATTR_TIME_STAMP.equals(name);
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(ATTR_MESSAGE, message);
        result.put(ATTR_TIME_STAMP, timeStamp);

        return Collections.unmodifiableMap(result);
    }

    public abstract MidiEventCategory getMidiCategory();

    public abstract String getMessageString();
}
