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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.midi.event.MidiShortMessageEvent;

/**
 * A set of MIDI utility methods.
 */
public abstract class MidiUtils {

    private static final Logger logger = LoggerFactory.getLogger(MidiUtils.class);

    /** Note names. */
    private static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    /**
     * Returns all available MIDI devices.
     *
     * @return all available MIDI devices.
     */
    public static List<MidiDevice> getDevices() {
        return Arrays.stream(MidiSystem.getMidiDeviceInfo()).map(info -> {
            try {
                return MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                throw SpongeUtils.wrapException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Returns the default input MIDI device.
     *
     * @return the default input MIDI device or {@code null} if not found.
     */
    public static MidiDevice getDefaultInputDevice() {
        try {
            Transmitter transmitter = MidiSystem.getTransmitter();

            if (transmitter != null && transmitter instanceof MidiDeviceTransmitter) {
                return ((MidiDeviceTransmitter) transmitter).getMidiDevice();
            }

            return null;
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Returns the MIDI device by name.
     *
     * @param name the MIDI device name.
     *
     * @return the MIDI device or {@code null} if not found.
     */
    public static MidiDevice getDeviceByName(String name) {
        return getDevice(device -> device.getDeviceInfo().getName().equals(name));
    }

    /**
     * Searches for a MIDI device by a predicate.
     *
     * @param predicate the predicate.
     *
     * @return the MIDI device or {@code null} if not found.
     */
    public static MidiDevice getDevice(Predicate<? super MidiDevice> predicate) {
        Optional<MidiDevice> result = getDevices().stream().filter(predicate).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    /**
     * Returns the map of all available MIDI devices.
     *
     * @return the map of all available MIDI devices.
     */
    public static Map<MidiDevice.Info, MidiDevice> getDeviceMap() {
        Map<MidiDevice.Info, MidiDevice> result = new LinkedHashMap<>();

        try {
            for (Info info : MidiSystem.getMidiDeviceInfo()) {
                result.put(info, MidiSystem.getMidiDevice(info));
            }
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        }

        return result;
    }

    /**
     * Opens the MIDI device.
     *
     * @param device the MIDI device.
     */
    public static void open(MidiDevice device) {
        if (device != null && !device.isOpen()) {
            try {
                device.open();
            } catch (MidiUnavailableException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    /**
     * Closes the MIDI device.
     *
     * @param device the MIDI device.
     */
    public static void close(MidiDevice device) {
        try {
            if (device != null && device.isOpen()) {
                device.close();
            }
        } catch (Exception e) {
            logger.warn("close", e);
        }
    }

    /**
     * Closes the MIDI transmitter.
     *
     * @param transmitter the MIDI transmitter.
     */
    public static void close(Transmitter transmitter) {
        try {
            if (transmitter != null) {
                transmitter.close();
            }
        } catch (Exception e) {
            logger.warn("close", e);
        }
    }

    /**
     * Closes the MIDI receiver.
     *
     * @param receiver the MIDI receiver.
     */
    public static void close(Receiver receiver) {
        try {
            if (receiver != null) {
                receiver.close();
            }
        } catch (Exception e) {
            logger.warn("close", e);
        }
    }

    /**
     * Creates a new MIDI short message.
     *
     * @param command the MIDI short message command.
     * @param channel the MIDI short message channel.
     * @param data1 the MIDI short message data1.
     * @param data2 the MIDI short message data2.
     * @return the MIDI short message.
     */
    public static ShortMessage createShortMessage(int command, int channel, int data1, int data2) {
        ShortMessage result = new ShortMessage();
        try {
            result.setMessage(command, channel, data1, data2);
        } catch (InvalidMidiDataException e) {
            throw SpongeUtils.wrapException(e);
        }

        return result;
    }

    /**
     * Sets the mute flag for all tracks in the sequence.
     *
     * @param sequencer the sequencer.
     * @param sequence the sequence.
     * @param mute the mute flag.
     */
    public static void setMute(Sequencer sequencer, Sequence sequence, boolean mute) {
        Track[] tracks = sequence.getTracks();
        for (int i = 0; i < tracks.length; i++) {
            sequencer.setTrackMute(i, mute);
        }
    }

    /**
     * Create a new Sponge MIDI event.
     *
     * @param midiPlugin the MIDI plugin.
     * @param message the MIDI short message.
     * @param timeStamp the MIDI timestamp.
     * @return the Sponge MIDI event.
     */
    public static MidiShortMessageEvent createShortMessageEvent(MidiPlugin midiPlugin, ShortMessage message, long timeStamp) {
        return new MidiShortMessageEvent(midiPlugin.getMidiShortMessageEventName(),
                midiPlugin.getEngine().getDefaultParameters().getEventClonePolicy(), message, timeStamp);
    }

    /**
     * Create a new Sponge MIDI event.
     *
     * @param midiPlugin the MIDI plugin.
     * @param message the MIDI short message.
     * @return the Sponge MIDI event.
     */
    public static MidiShortMessageEvent createShortMessageEvent(MidiPlugin midiPlugin, ShortMessage message) {
        return createShortMessageEvent(midiPlugin, message, MidiConstants.NO_TIME_STAMP);
    }

    /**
     * Returns the note representation for the given MIDI key. Middle C (key 60) is represented as C5.
     *
     * @param key the MIDI key.
     * @return the note representation.
     */
    public static String getKeyNote(int key) {
        if (key < 0 || key > 127) {
            throw new IllegalArgumentException("MIDI key is " + key + " but should be a value between 0 and 127");
        }

        return NOTE_NAMES[key % 12] + key / 12;
    }

    /**
     * Loads a MIDI instrument by name. Throws exception if not found.
     *
     * @param synthesizer the synthesizer.
     * @param instrumentName the instrument name.
     * @return the instrument or {@code null} if the instrument couldn't be loaded.
     */
    public static Instrument loadInstrument(Synthesizer synthesizer, String instrumentName) {
        Optional<Instrument> result = Arrays.stream(synthesizer.getAvailableInstruments())
                .filter(instrument -> instrument.getName().equals(instrumentName)).findFirst();

        if (!result.isPresent()) {
            throw new IllegalArgumentException("Instrument named " + instrumentName + " is not available");
        }

        return synthesizer.loadInstrument(result.get()) ? result.get() : null;
    }

    /**
     * Returns the MIDI channel.
     *
     * @param synthesizer the synthesizer.
     * @param channel the channel number.
     * @param required if {@code true} then throws exception when not found or not available. Otherwise returns {@code null}.
     * @return the MIDI channel.
     */
    public static MidiChannel getChannel(Synthesizer synthesizer, int channel, boolean required) {
        MidiChannel[] channels = synthesizer.getChannels();
        if (channel >= channels.length || channels[channel] == null) {
            if (required) {
                throw new IllegalArgumentException("Channel " + channel + " is not available");
            } else {
                return null;
            }
        }

        return channels[channel];
    }

    /**
     * Returns the MIDI instrument.
     *
     * @param synthesizer the synthesizer.
     * @param channel the channel number.
     * @return the MIDI instrument.
     */
    public static Instrument getInstrument(Synthesizer synthesizer, int channel) {
        return synthesizer.getAvailableInstruments()[getChannel(synthesizer, channel, true).getProgram()];
    }

    /**
     * Sets the synthesizer instrument from the currently selected bank of instruments.
     *
     * @param synthesizer the synthesizer.
     * @param channel the channel.
     * @param instrument the instrument number.
     * @return the new instrument.
     */
    public static Instrument setInstrument(Synthesizer synthesizer, int channel, int instrument) {
        getChannel(synthesizer, channel, true).programChange(instrument);

        return getInstrument(synthesizer, channel);
    }

    /**
     * Sets the synthesizer instrument from the currently selected bank of instruments.
     *
     * @param synthesizer the synthesizer.
     * @param channel the channel.
     * @param instrumentName the instrument name.
     * @return the new instrument.
     */
    public static Instrument setInstrument(Synthesizer synthesizer, int channel, String instrumentName) {
        Optional<Instrument> result = Arrays.stream(synthesizer.getAvailableInstruments())
                .filter(instrument -> instrument.getName().equals(instrumentName)).findFirst();

        if (!result.isPresent()) {
            throw new IllegalArgumentException("Instrument named " + instrumentName + " is not available");
        }

        return setInstrument(synthesizer, channel, result.get().getPatch().getProgram());
    }

    /**
     * Loads all instruments to the synthesizer from the soundbank.
     *
     * @param synthesizer the synthesizer.
     * @param soundbank the soundbank.
     * @return {@code true} on success.
     */
    public static boolean loadAllInstruments(Synthesizer synthesizer, Soundbank soundbank) {
        return synthesizer.loadAllInstruments(soundbank);
    }

    /**
     * Loads all instruments to the synthesizer from the default soundbank.
     *
     * @param synthesizer the synthesizer.
     * @return {@code true} on success.
     */
    public static boolean loadAllInstruments(Synthesizer synthesizer) {
        if (synthesizer.getDefaultSoundbank() != null) {
            return loadAllInstruments(synthesizer, synthesizer.getDefaultSoundbank());
        } else {
            return false;
        }
    }

    /**
     * Returns the synthesizer loaded instruments.
     *
     * @param synthesizer the synthesizer.
     * @return the instruments.
     */
    public static List<Instrument> getInstruments(Synthesizer synthesizer) {
        return Arrays.stream(synthesizer.getLoadedInstruments()).collect(Collectors.toList());
    }

    /**
     * Closes all transmitter of the device.
     *
     * @param device the MIDI device.
     */
    public static void closeTransmitters(MidiDevice device) {
        device.getTransmitters().forEach(Transmitter::close);
    }

    /**
     * Returns the first supported MIDI file type.
     *
     * @param sequence the sequence;
     * @return the supported MIDI file type.
     */
    public static int getFirstSupportedMidiFileType(Sequence sequence) {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);

        if (fileTypes.length == 0) {
            throw new SpongeException("No supported MIDI file format");
        }
        return fileTypes[0];
    }

    private MidiUtils() {
        //
    }
}
