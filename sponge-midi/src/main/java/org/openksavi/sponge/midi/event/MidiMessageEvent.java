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
public abstract class MidiMessageEvent<T extends MidiMessage> extends BaseEvent {

    private static final long serialVersionUID = 4119731368862161071L;

    /** The MIDI message attribute name. */
    public static final String ATTR_MESSAGE = "message";

    /** The MIDI timeStamp attribute name. */
    public static final String ATTR_TIME_STAMP = "timeStamp";

    /** The MIDI message. */
    private T message;

    /** The MIDI timeStamp. */
    private Long timeStamp;

    /**
     * A MIDI-based Sponge event category.
     */
    public static enum Category {
        SHORT("short"), META("meta"), SYSEX("sysex");

        private String code;

        private Category(String code) {
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
    public MidiMessageEvent(String name, EventClonePolicy clonePolicy, T message, Long timeStamp) {
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
    public MidiMessageEvent(String name, EventClonePolicy clonePolicy, T message) {
        this(name, clonePolicy, message, null);
    }

    /**
     * Returns the MIDI message.
     *
     * @return the MIDI message.
     */
    public T getMessage() {
        return message;
    }

    /**
     * Sets the MIDI message.
     *
     * @param message the MIDI message.
     */
    public void setMessage(T message) {
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
     * @param message the MIDI timeStamp.
     */
    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public Object get(String name) {
        switch (name) {
        case ATTR_MESSAGE:
            return message;
        case ATTR_TIME_STAMP:
            return timeStamp;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MidiMessageEvent<T> set(String name, Object value) {
        switch (name) {
        case ATTR_MESSAGE:
            message = (T) value;
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

    public abstract Category getCategory();

    public abstract String getMessageString();
}
