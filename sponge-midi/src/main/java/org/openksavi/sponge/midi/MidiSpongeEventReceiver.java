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

package org.openksavi.sponge.midi;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.midi.event.MidiMessageEvent;
import org.openksavi.sponge.midi.event.MidiMetaMessageEvent;
import org.openksavi.sponge.midi.event.MidiSysexMessageEvent;

/**
 * A MIDI receiver that send MIDI messages to the Sponge engine as events.
 */
public class MidiSpongeEventReceiver implements Receiver {

    private static final Logger logger = LoggerFactory.getLogger(MidiSpongeEventReceiver.class);

    /** The MIDI plugin. */
    private MidiPlugin midiPlugin;

    /** If {@code true} then generates the sound using the {@code sound} method in the {@code midiPlugin} before sending an event. */
    private boolean sound = false;

    /**
     * Creates a new MIDI receiver.
     *
     * @param midiPlugin the MIDI plugin.
     */
    public MidiSpongeEventReceiver(MidiPlugin midiPlugin) {
        this.midiPlugin = midiPlugin;
    }

    /**
     * Creates a new MIDI receiver.
     *
     * @param midiPlugin the MIDI plugin.
     * @param sound if {@code true} then generates the sound using the {@code sound} method in the {@code midiPlugin} before sending an
     *        event.
     */
    public MidiSpongeEventReceiver(MidiPlugin midiPlugin, boolean sound) {
        this(midiPlugin);

        this.sound = sound;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        try {
            if (sound) {
                midiPlugin.sound(message);
            }

            MidiMessageEvent<?> event;
            if (message instanceof ShortMessage) {
                event = MidiUtils.createShortMessageEvent(midiPlugin, (ShortMessage) message, timeStamp);
            } else if (message instanceof SysexMessage) {
                event = new MidiSysexMessageEvent(midiPlugin.getMidiSysexMessageEventName(),
                        midiPlugin.getEngine().getDefaultParameters().getEventClonePolicy(), (SysexMessage) message, timeStamp);
            } else if (message instanceof MetaMessage) {
                event = new MidiMetaMessageEvent(midiPlugin.getMidiMetaMessageEventName(),
                        midiPlugin.getEngine().getDefaultParameters().getEventClonePolicy(), (MetaMessage) message, timeStamp);
            } else {
                throw new SpongeException("Unknown MIDI message type: " + message.getClass());
            }

            midiPlugin.getEngine().getOperations().event(event).send();
        } catch (Exception e) {
            logger.error("send", e);
        }
    }

    @Override
    public void close() {
        //
    }
}
