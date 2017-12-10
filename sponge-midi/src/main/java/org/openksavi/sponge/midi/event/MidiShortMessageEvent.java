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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A MIDI ShortMessage wrapped in a Sponge event.
 */
public class MidiShortMessageEvent extends MidiMessageEvent<ShortMessage> {

    private static final long serialVersionUID = -5881083348610116349L;

    /** The MIDI command attribute name. */
    public static final String ATTR_COMMAND = "command";

    /** The MIDI channel attribute name. */
    public static final String ATTR_CHANNEL = "channel";

    /** The MIDI data1 attribute name. */
    public static final String ATTR_DATA1 = "data1";

    /** The MIDI data2 attribute name. */
    public static final String ATTR_DATA2 = "data2";

    /**
     * Creates a new MIDI ShortMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI ShortMessage.
     * @param timeStamp the MIDI timeStamp.
     */
    public MidiShortMessageEvent(String name, EventClonePolicy clonePolicy, ShortMessage message, Long timeStamp) {
        super(name, clonePolicy, message, timeStamp);
    }

    /**
     * Creates a new MIDI ShortMessage Sponge event.
     *
     * @param name the event name.
     * @param clonePolicy the event clone policy.
     * @param message the MIDI ShortMessage.
     */
    public MidiShortMessageEvent(String name, EventClonePolicy clonePolicy, ShortMessage message) {
        this(name, clonePolicy, message, null);
    }

    /**
     * Returns the MIDI short message command.
     *
     * @return the MIDI short message command.
     */
    public int getCommand() {
        return getMessage().getCommand();
    }

    /**
     * Sets the MIDI short message command.
     *
     * @param command the MIDI short message command.
     */
    public void setCommand(int command) {
        try {
            getMessage().setMessage(command, getChannel(), getData1(), getData2());
        } catch (InvalidMidiDataException e) {
            throw Utils.wrapException("setCommand", e);
        }
    }

    /**
     * Returns the MIDI short message channel.
     *
     * @return the MIDI short message channel.
     */
    public int getChannel() {
        return getMessage().getChannel();
    }

    /**
     * Sets the MIDI short message channel.
     *
     * @param channel the MIDI short message channel.
     */
    public void setChannel(int channel) {
        try {
            getMessage().setMessage(getCommand(), channel, getData1(), getData2());
        } catch (InvalidMidiDataException e) {
            throw Utils.wrapException("setChannel", e);
        }
    }

    /**
     * Returns the MIDI short message data1.
     *
     * @return the MIDI short message data1.
     */
    public int getData1() {
        return getMessage().getData1();
    }

    /**
     * Sets the MIDI short message data1.
     *
     * @param data1 the MIDI short message data1.
     */
    public void setData1(int data1) {
        try {
            getMessage().setMessage(getCommand(), getChannel(), data1, getData2());
        } catch (InvalidMidiDataException e) {
            throw Utils.wrapException("setData1", e);
        }
    }

    /**
     * Returns the MIDI short message data2.
     *
     * @return the MIDI short message data2.
     */
    public int getData2() {
        return getMessage().getData2();
    }

    /**
     * Sets the MIDI short message data2.
     *
     * @param data2 the MIDI short message data2.
     */
    public void setData2(int data2) {
        try {
            getMessage().setMessage(getCommand(), getChannel(), getData1(), data2);
        } catch (InvalidMidiDataException e) {
            throw Utils.wrapException("setData2", e);
        }
    }

    @Override
    public Object get(String name) {
        switch (name) {
        case ATTR_COMMAND:
            return getCommand();
        case ATTR_CHANNEL:
            return getChannel();
        case ATTR_DATA1:
            return getData1();
        case ATTR_DATA2:
            return getData2();
        default:
            return super.get(name);
        }
    }

    @Override
    public MidiShortMessageEvent set(String name, Object value) {
        switch (name) {
        case ATTR_COMMAND:
            setCommand(Utils.toInt(value));
            break;
        case ATTR_CHANNEL:
            setChannel(Utils.toInt(value));
            break;
        case ATTR_DATA1:
            setData1(Utils.toInt(value));
            break;
        case ATTR_DATA2:
            setData2(Utils.toInt(value));
            break;
        default:
            super.set(name, value);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_COMMAND.equals(name) || ATTR_CHANNEL.equals(name) || ATTR_DATA1.equals(name) || ATTR_DATA2.equals(name)
                || super.has(name);
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(ATTR_COMMAND, getCommand());
        result.put(ATTR_CHANNEL, getChannel());
        result.put(ATTR_DATA1, getData1());
        result.put(ATTR_DATA2, getData2());
        result.putAll(super.getAll());

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Category getCategory() {
        return Category.SHORT;
    }

    @Override
    public String getMessageString() {
        //@formatter:off
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("category", getCategory().getCode())
                .append(ATTR_COMMAND, getCommand())
                .append(ATTR_CHANNEL, getChannel())
                .append(ATTR_DATA1, getData1())
                .append(ATTR_DATA2, getData2())
                .append(ATTR_TIME_STAMP, getTimeStamp())
                .toString();
        //@formatter:on
    }
}
