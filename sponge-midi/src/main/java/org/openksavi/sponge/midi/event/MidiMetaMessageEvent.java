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
import javax.sound.midi.MetaMessage;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A MIDI MetaMessage wrapped in a Sponge event.
 */
public class MidiMetaMessageEvent extends MidiMessageEvent<MetaMessage> {

    private static final long serialVersionUID = -8321799970247326821L;

    /** The MIDI type attribute name. */
    public static final String ATTR_MESSAGE_TYPE = "messageType";

    /** The MIDI data attribute name. */
    public static final String ATTR_DATA = "data";

    /**
     * Creates a new MIDI MetaMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI MetaMessage.
     * @param timeStamp the MIDI timeStamp.
     */
    public MidiMetaMessageEvent(String name, EventClonePolicy clonePolicy, MetaMessage message, Long timeStamp) {
        super(name, clonePolicy, message, timeStamp);
    }

    /**
     * Creates a new MIDI MetaMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI MetaMessage.
     */
    public MidiMetaMessageEvent(String name, EventClonePolicy clonePolicy, MetaMessage message) {
        this(name, clonePolicy, message, null);
    }

    /**
     * Returns the MIDI MetaMessage type.
     *
     * @return the MIDI MetaMessage type.
     */
    public int getMessageType() {
        return getMessage().getType();
    }

    /**
     * Sets the MIDI MetaMessage type.
     *
     * @param type the MIDI MetaMessage type.
     */
    public void setMessageType(int type) {
        try {
            getMessage().setMessage(type, getData(), getData().length);
        } catch (InvalidMidiDataException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Returns the MIDI MetaMessage data.
     *
     * @return the MIDI MetaMessage data.
     */
    public byte[] getData() {
        return getMessage().getData();
    }

    /**
     * Sets the MIDI MetaMessage data.
     *
     * @param data the MIDI MetaMessage data.
     */
    public void setData(byte[] data) {
        try {
            getMessage().setMessage(getMessageType(), data, data.length);
        } catch (InvalidMidiDataException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        Object result;
        switch (name) {
        case ATTR_MESSAGE_TYPE:
            result = getMessageType();
            break;
        case ATTR_DATA:
            result = getData();
            break;
        default:
            result = super.get(name);
            break;
        }

        return (T) result;
    }

    @Override
    public MidiMetaMessageEvent set(String name, Object value) {
        switch (name) {
        case ATTR_MESSAGE_TYPE:
            setMessageType(SpongeUtils.toInt(value));
            break;
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
        return ATTR_MESSAGE_TYPE.equals(name) || ATTR_DATA.equals(name) || super.has(name);
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(ATTR_MESSAGE_TYPE, getMessageType());
        result.put(ATTR_DATA, Hex.encodeHex(getData()));
        result.putAll(super.getAll());

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Category getCategory() {
        return Category.META;
    }

    @Override
    public String getMessageString() {
        //@formatter:off
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("category", getCategory().getCode())
                .append(ATTR_MESSAGE_TYPE, getMessageType())
                .append(ATTR_DATA, Hex.encodeHex(getData()))
                .append(ATTR_TIME_STAMP, getTimeStamp())
                .toString();
        //@formatter:on
    }
}
