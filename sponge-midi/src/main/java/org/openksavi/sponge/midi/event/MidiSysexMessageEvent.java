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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A MIDI SysexMessage wrapped in a Sponge event.
 */
public class MidiSysexMessageEvent extends MidiMessageEvent<SysexMessage> {

    private static final long serialVersionUID = 3060906955262924451L;

    /** The MIDI data attribute name. */
    public static final String ATTR_DATA = "data";

    /**
     * Creates a new MIDI SysexMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI SysexMessage.
     * @param timeStamp the MIDI timeStamp.
     */
    public MidiSysexMessageEvent(String name, EventClonePolicy clonePolicy, SysexMessage message, Long timeStamp) {
        super(name, clonePolicy, message, timeStamp);
    }

    /**
     * Creates a new MIDI SysexMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI SysexMessage.
     */
    public MidiSysexMessageEvent(String name, EventClonePolicy clonePolicy, SysexMessage message) {
        this(name, clonePolicy, message, null);
    }

    /**
     * Returns the MIDI SysexMessage data.
     *
     * @return the MIDI SysexMessage data.
     */
    public byte[] getData() {
        return getMessage().getData();
    }

    /**
     * Sets the MIDI SysexMessage data.
     *
     * @param data the MIDI SysexMessage data.
     */
    public void setData(byte[] data) {
        try {
            getMessage().setMessage(data, data.length);
        } catch (InvalidMidiDataException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        switch (name) {
        case ATTR_DATA:
            return (T) getData();
        default:
            return super.get(name);
        }
    }

    @Override
    public MidiSysexMessageEvent set(String name, Object value) {
        switch (name) {
        case ATTR_DATA:
            setData((byte[]) value);
            break;
        default:
            super.set(name, value);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_DATA.equals(name) || super.has(name);
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(ATTR_DATA, getData());
        result.putAll(super.getAll());

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Category getCategory() {
        return Category.SYSEX;
    }

    @Override
    public String getMessageString() {
        //@formatter:off
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("category", getCategory().getCode())
                .append(ATTR_DATA, Hex.encodeHex(getData()))
                .append(ATTR_TIME_STAMP, getTimeStamp())
                .toString();
        //@formatter:on
    }
}
