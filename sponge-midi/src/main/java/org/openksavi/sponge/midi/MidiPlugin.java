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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.midi.event.MidiMetaMessageEvent;
import org.openksavi.sponge.midi.event.MidiShortMessageEvent;

/**
 * MIDI plugin.
 */
public class MidiPlugin extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MidiPlugin.class);

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "midi";

    /** The sequencer. */
    private Sequencer sequencer;

    /** The sequencer transmitter. */
    private Transmitter sequencerTransmitter;

    /** The synthesizer. */
    private Synthesizer synthesizer;

    /** The synthesizer receiver. */
    private Receiver synthesizerReceiver;

    /** The input device. */
    private MidiDevice inputDevice;

    /** The input device transmitter. */
    private Transmitter inputTransmitter;

    /**
     * If {@code true} then the default sequencer will be connected to the default synthesizer (e.g. to generate sound while playing MIDI
     * files). The default value is {@code false}.
     */
    private boolean sequencerConnectedToSynthesizer = false;

    /** If {@code true} then all instruments in the default soundbank will be loaded at startup. The default value is {@code true}. */
    private boolean loadAllInstruments = true;

    /** A name of a MIDI ShortMessage Sponge event sent by this plugin to the engine. */
    private String midiShortMessageEventName = MidiConstants.DEFAULT_SHORT_MESSAGE_EVENT_NAME;

    /** A name of a MIDI SysexMessage Sponge event sent by this plugin to the engine. */
    private String midiSysexMessageEventName = MidiConstants.DEFAULT_SYSEX_MESSAGE_EVENT_NAME;

    /** A name of a MIDI MetaMessage Sponge event sent by this plugin to the engine. */
    private String midiMetaMessageEventName = MidiConstants.DEFAULT_META_MESSAGE_EVENT_NAME;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new MIDI plugin.
     */
    public MidiPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new MIDI plugin.
     *
     * @param name the plugin name.
     */
    public MidiPlugin(String name) {
        super(name);
    }

    /**
     * Applies the XML configuration to this plugin.
     */
    @Override
    public void onConfigure(Configuration configuration) {
        sequencerConnectedToSynthesizer =
                configuration.getBoolean(MidiConstants.TAG_SEQUENCER_CONNECTED_TO_SYNTHESIZER, sequencerConnectedToSynthesizer);
        loadAllInstruments = configuration.getBoolean(MidiConstants.TAG_LOAD_ALL_INSTRUMENTS, loadAllInstruments);

        midiShortMessageEventName = configuration.getString(MidiConstants.TAG_MIDI_SHORT_MESSAGE_EVENT_NAME, midiShortMessageEventName);
        midiSysexMessageEventName = configuration.getString(MidiConstants.TAG_MIDI_SYSEX_MESSAGE_EVENT_NAME, midiSysexMessageEventName);
        midiMetaMessageEventName = configuration.getString(MidiConstants.TAG_MIDI_META_MESSAGE_EVENT_NAME, midiMetaMessageEventName);
    }

    /**
     * Starts up this plugin. Obtains and opens the required MIDI devices and sets up receivers and transmitters. Any setters for MIDI
     * devices must be invoked before the Sponge startup.
     */
    @Override
    public void onStartup() {
        try {
            logMidiSystemInfo();

            // Use the default sequencer if not set.
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer(sequencerConnectedToSynthesizer);
                if (sequencer == null) {
                    throw new SpongeException("The sequencer device is not supported");
                }
            }

            // Use the default synthesizer if not set.
            if (synthesizer == null) {
                synthesizer = MidiSystem.getSynthesizer();
                if (synthesizer == null) {
                    throw new SpongeException("The synthesizer device is not supported");
                }
            }

            update();

            if (loadAllInstruments) {
                loadAllInstruments();
            }
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Updates the plugin state, e.g. device wiring using transmitters and receivers.
     */
    public void update() {
        updateSequencer();
        updateSynthesizer();
        updateInputDevice();
    }

    /**
     * Updates the sequencer state. This method should be invoked after setting the sequencer in the plugin.
     */
    public void updateSequencer() {
        lock.lock();
        try {
            MidiUtils.open(sequencer);

            // Close existing and create a sequencer transmitter that sends Sponge events.
            MidiUtils.close(sequencerTransmitter);
            if (sequencer != null) {
                sequencerTransmitter = sequencer.getTransmitter();
                sequencerTransmitter.setReceiver(new MidiSpongeEventReceiver(this));

                sequencer.addMetaEventListener(message -> {
                    getEngine().getOperations().event(new MidiMetaMessageEvent(getMidiMetaMessageEventName(),
                            getEngine().getDefaultParameters().getEventClonePolicy(), message)).send();
                });
            }
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates the synthesizer state. This method should be invoked after setting the synthesizer in the plugin.
     */
    public void updateSynthesizer() {
        lock.lock();
        try {
            MidiUtils.open(synthesizer);

            // Close existing and create a new synthesizer receiver to generate sounds.
            MidiUtils.close(synthesizerReceiver);
            if (synthesizer != null) {
                synthesizerReceiver = synthesizer.getReceiver();
            }
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates the input device state. This method should be invoked after setting the input device in the plugin.
     */
    public void updateInputDevice() {
        lock.lock();
        try {
            MidiUtils.open(inputDevice);

            // Close existing and create an input device transmitter that sends Sponge events.
            MidiUtils.close(inputTransmitter);
            if (inputDevice != null) {
                inputTransmitter = inputDevice.getTransmitter();
                inputTransmitter.setReceiver(new MidiSpongeEventReceiver(this));
            }
        } catch (MidiUnavailableException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Shuts down the plugin. Closes the MIDI devices.
     */
    @Override
    public void onShutdown() {
        MidiUtils.close(sequencer);
        MidiUtils.close(synthesizer);
        MidiUtils.close(inputDevice);
    }

    /**
     * Sets and updates the input MIDI device in the plugin. If there has already been connected an another input device then replaces it.
     *
     * @param deviceName the input MIDI device name.
     */
    public void connectInputDevice(String deviceName) {
        MidiDevice device = getDeviceByName(deviceName);
        if (device == null) {
            throw new SpongeException("MIDI device named '" + deviceName + "' not found");
        }

        setInputDevice(device);
        updateInputDevice();
    }

    /**
     * Sets and updates the default input MIDI device in the plugin. If there has already been connected an another input device then
     * replaces it.
     */
    public void connectDefaultInputDevice() {
        MidiDevice device = MidiUtils.getDefaultInputDevice();
        if (device == null) {
            throw new SpongeException("No default input MIDI device found");
        }

        setInputDevice(device);
        updateInputDevice();
    }

    /**
     * Returns the sequencer.
     *
     * @return the sequencer.
     */
    public Sequencer getSequencer() {
        return sequencer;
    }

    /**
     * Sets the sequencer. If there has already been an another sequencer set then it will be closed.
     *
     * @param sequencer the sequencer.
     */
    public void setSequencer(Sequencer sequencer) {
        if (sequencer != this.sequencer) {
            MidiUtils.close(this.sequencer);
        }

        this.sequencer = sequencer;
    }

    /**
     * Returns the synthesizer.
     *
     * @return the synthesizer.
     */
    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    /**
     * Sets the synthesizer. If there has already been an another synthesizer set then it will be closed.
     *
     * @param synthesizer the synthesizer.
     */
    public void setSynthesizer(Synthesizer synthesizer) {
        if (synthesizer != this.synthesizer) {
            MidiUtils.close(this.synthesizer);
        }

        this.synthesizer = synthesizer;
    }

    /**
     * Returns the input device.
     *
     * @return the input device.
     */
    public MidiDevice getInputDevice() {
        return inputDevice;
    }

    /**
     * Sets the input device. If there has already been an another input device set then it will be closed.
     *
     * @param inputDevice the input device.
     */
    public void setInputDevice(MidiDevice inputDevice) {
        if (inputDevice != this.inputDevice) {
            MidiUtils.close(this.inputDevice);
        }

        this.inputDevice = inputDevice;
    }

    /**
     * Returns the synthesizer receiver.
     *
     * @return the synthesizer receiver.
     */
    public Receiver getSynthesizerReceiver() {
        return synthesizerReceiver;
    }

    /**
     * Returns the input device transmitter.
     *
     * @return the input device transmitter.
     */
    public Transmitter getInputTransmitter() {
        return inputTransmitter;
    }

    /**
     * Returns the sequencer transmitter.
     *
     * @return the sequencer transmitter.
     */
    public Transmitter getSequencerTransmitter() {
        return sequencerTransmitter;
    }

    /**
     * Returns the default sequencer connected to the default synthesizer behavior.
     *
     * @return {@code true} if the default sequencer will be connected to the default synthesizer (e.g. to generate sound while playing MIDI
     *         files)
     */
    public boolean isSequencerConnectedToSynthesizer() {
        return sequencerConnectedToSynthesizer;
    }

    /**
     * Sets the default sequencer connected to the default synthesizer behavior.
     *
     * @param sequencerConnectedToSynthesizer if {@code true} then the default sequencer will be connected to the default synthesizer (e.g.
     *        to generate sound while playing MIDI files)
     */
    public void setSequencerConnectedToSynthesizer(boolean sequencerConnectedToSynthesizer) {
        this.sequencerConnectedToSynthesizer = sequencerConnectedToSynthesizer;
    }

    /**
     * Returns the load all instruments behavior.
     *
     * @return {@code true} if all instruments in the default soundbank will be loaded at startup.
     */
    public boolean isLoadAllInstruments() {
        return loadAllInstruments;
    }

    /**
     * Sets the load all instruments behavior.
     *
     * @param loadAllInstruments if {@code true} then all instruments in the default soundbank will be loaded at startup.
     */
    public void setLoadAllInstruments(boolean loadAllInstruments) {
        this.loadAllInstruments = loadAllInstruments;
    }

    /**
     * Returns a name of a MIDI ShortMessage Sponge event sent by this plugin to the engine.
     *
     * @return a name of a MIDI ShortMessage Sponge event sent by this plugin to the engine.
     */
    public String getMidiShortMessageEventName() {
        return midiShortMessageEventName;
    }

    /**
     * Sets a name of a MIDI ShortMessage Sponge event sent by this plugin to the engine.
     *
     * @param midiShortMessageEventName a name of a MIDI ShortMessage Sponge event sent by this plugin to the engine.
     */
    public void setMidiShortMessageEventName(String midiShortMessageEventName) {
        this.midiShortMessageEventName = midiShortMessageEventName;
    }

    /**
     * Returns a name of a MIDI SysexMessage Sponge event sent by this plugin to the engine.
     *
     * @return a name of a MIDI SysexMessage Sponge event sent by this plugin to the engine.
     */
    public String getMidiSysexMessageEventName() {
        return midiSysexMessageEventName;
    }

    /**
     * Sets a name of a MIDI SysexMessage Sponge event sent by this plugin to the engine.
     *
     * @param midiSysexMessageEventName a name of a MIDI SysexMessage Sponge event sent by this plugin to the engine.
     */
    public void setMidiSysexMessageEventName(String midiSysexMessageEventName) {
        this.midiSysexMessageEventName = midiSysexMessageEventName;
    }

    /**
     * Returns a name of a MIDI MetaMessage Sponge event sent by this plugin to the engine.
     *
     * @return a name of a MIDI MetaMessage Sponge event sent by this plugin to the engine.
     */
    public String getMidiMetaMessageEventName() {
        return midiMetaMessageEventName;
    }

    /**
     * Sets a name of a MIDI MetaMessage Sponge event sent by this plugin to the engine.
     *
     * @param midiMetaMessageEventName a name of a MIDI MetaMessage Sponge event sent by this plugin to the engine.
     */
    public void setMidiMetaMessageEventName(String midiMetaMessageEventName) {
        this.midiMetaMessageEventName = midiMetaMessageEventName;
    }

    /**
     * Generates a sound by sending the MIDI message to the synthesizer receiver.
     *
     * @param message the MIDI message.
     */
    public void sound(MidiMessage message) {
        sound(message, MidiConstants.NO_TIME_STAMP);
    }

    /**
     * Generates a sound by sending the MIDI message to the synthesizer receiver.
     *
     * @param message the MIDI message.
     * @param timeStamp the message timestamp.
     */
    public void sound(MidiMessage message, long timeStamp) {
        synthesizerReceiver.send(message, timeStamp);
    }

    /**
     * Generates a sound by sending the short MIDI message to the synthesizer receiver.
     *
     * @param command the MIDI message command.
     * @param channel the MIDI message channel.
     * @param data1 the MIDI message data1.
     * @param data2 the MIDI message data2.
     */
    public void sound(int command, int channel, int data1, int data2) {
        sound(createShortMessage(command, channel, data1, data2));
    }

    /**
     * Sends a {@code note on} MIDI message to the synthesizer receiver.
     *
     * @param channel the MIDI message channel.
     * @param data1 the MIDI message data1.
     * @param data2 the MIDI message data2.
     */
    public void noteOn(int channel, int data1, int data2) {
        sound(createShortMessage(ShortMessage.NOTE_ON, channel, data1, data2));
    }

    /**
     * Sends a {@code note off} MIDI message to the synthesizer receiver.
     *
     * @param channel the MIDI message channel.
     * @param data1 the MIDI message data1.
     * @param data2 the MIDI message data2.
     */
    public void noteOff(int channel, int data1, int data2) {
        sound(createShortMessage(ShortMessage.NOTE_OFF, channel, data1, data2));
    }

    /**
     * Starts playing the MIDI file using the sequencer.
     *
     * @param midiFilename the MIDI filename.
     */
    public void startPlay(String midiFilename) {
        startPlay(new File(midiFilename));
    }

    /**
     * Starts playing the MIDI file using the sequencer.
     *
     * @param midiFile the MIDI file.
     */
    public void startPlay(File midiFile) {
        try {
            startPlay(MidiSystem.getSequence(midiFile));
        } catch (InvalidMidiDataException | IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Starts playing the MIDI file using the sequencer.
     *
     * @param stream the MIDI file stream.
     */
    public void startPlay(InputStream stream) {
        try {
            startPlay(MidiSystem.getSequence(stream));
        } catch (InvalidMidiDataException | IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Starts playing the MIDI file using the sequencer.
     *
     * @param url the MIDI file url.
     */
    public void startPlay(URL url) {
        try {
            startPlay(MidiSystem.getSequence(url));
        } catch (InvalidMidiDataException | IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Starts playing the MIDI file using the sequencer.
     *
     * @param sequence the MIDI sequence.
     */
    public void startPlay(Sequence sequence) {
        try {
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            throw SpongeUtils.wrapException(e);
        }
        sequencer.start();
    }

    /**
     * Stops playing.
     */
    public void stopPlay() {
        sequencer.stop();
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
    public ShortMessage createShortMessage(int command, int channel, int data1, int data2) {
        return MidiUtils.createShortMessage(command, channel, data1, data2);
    }

    /**
     * Create a new Sponge MIDI ShortMessage event.
     *
     * @param message the MIDI short message.
     * @param timeStamp the MIDI timestamp.
     * @return the Sponge MIDI event.
     */
    public MidiShortMessageEvent createShortMessageEvent(ShortMessage message, long timeStamp) {
        return MidiUtils.createShortMessageEvent(this, message, timeStamp);
    }

    /**
     * Create a new Sponge MIDI ShortMessage event.
     *
     * @param message the MIDI short message.
     * @return the Sponge MIDI event.
     */
    public MidiShortMessageEvent createShortMessageEvent(ShortMessage message) {
        return MidiUtils.createShortMessageEvent(this, message);
    }

    /**
     * Loads all instruments in the default soundbank to the synthesizer.
     *
     * @return {@code true} on success.
     */
    public boolean loadAllInstruments() {
        return MidiUtils.loadAllInstruments(synthesizer);
    }

    /**
     * Sets the synthesizer instrument.
     *
     * @param channel the channel.
     * @param instrument the instrument number.
     * @return the new instrument.
     */
    public Instrument setInstrument(int channel, int instrument) {
        return MidiUtils.setInstrument(synthesizer, channel, instrument);
    }

    /**
     * Sets the synthesizer instrument.
     *
     * @param channel the channel.
     * @param instrumentName the instrument name.
     * @return the new instrument.
     */
    public Instrument setInstrument(int channel, String instrumentName) {
        return MidiUtils.setInstrument(synthesizer, channel, instrumentName);
    }

    /**
     * Returns the loaded synthesizer instruments.
     *
     * @return the instruments.
     */
    public List<Instrument> getInstruments() {
        return MidiUtils.getInstruments(synthesizer);
    }

    /**
     * Returns the selected instrument for the specified channel.
     *
     * @param channel the channel number.
     * @return the instrument.
     */
    public Instrument getInstrument(int channel) {
        return MidiUtils.getInstrument(synthesizer, channel);
    }

    /**
     * Returns the synthesizer channels.
     *
     * @return the synthesizer channels.
     */
    public List<MidiChannel> getChannels() {
        return Arrays.asList(synthesizer.getChannels());
    }

    /**
     * Returns all available MIDI devices.
     *
     * @return all available MIDI devices.
     */
    public List<MidiDevice> getDevices() {
        return MidiUtils.getDevices();
    }

    /**
     * Returns the default input MIDI device.
     *
     * @return the default input MIDI device.
     */
    public MidiDevice getDefaultInputDevice() {
        return MidiUtils.getDefaultInputDevice();
    }

    /**
     * Returns the MIDI device by name.
     *
     * @param name the MIDI device name.
     *
     * @return the MIDI device.
     */
    public MidiDevice getDeviceByName(String name) {
        return MidiUtils.getDeviceByName(name);
    }

    /**
     * Logs MIDI system info (e.g. MIDI devices).
     */
    public void logMidiSystemInfo() {
        getDevices().forEach(device -> logger.info(
                "Device: name={}, class={}, description={}, vendor={}, version={}, microsecondPosition={}, "
                        + "maxTransmitters={}, maxReceivers={}",
                device.getDeviceInfo().getName(), device.getClass().getSimpleName(), device.getDeviceInfo().getDescription(),
                device.getDeviceInfo().getVendor(), device.getDeviceInfo().getVersion(), device.getMicrosecondPosition(),
                device.getMaxTransmitters(), device.getMaxReceivers()));
    }

    /**
     * Logs available synthesizer instruments.
     */
    public void logInstruments() {
        for (Instrument instrument : synthesizer.getAvailableInstruments()) {
            logger.info("Instrument {} ({}/{})", instrument.getName(), instrument.getPatch().getBank(), instrument.getPatch().getProgram());
        }
    }
}
