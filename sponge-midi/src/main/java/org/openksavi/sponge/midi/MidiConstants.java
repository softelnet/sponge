package org.openksavi.sponge.midi;

/**
 * MIDI constants.
 */
public final class MidiConstants {

    /** The default name of a MIDI ShortMessage based Sponge event sent by the MIDI plugin to the engine. */
    public static final String DEFAULT_SHORT_MESSAGE_EVENT_NAME = "midiShort";

    /** The default name of a MIDI SysexMessage based Sponge event sent by the MIDI plugin to the engine. */
    public static final String DEFAULT_SYSEX_MESSAGE_EVENT_NAME = "midiSysex";

    /** The default name of a MIDI MetaMessage based Sponge event sent by the MIDI plugin to the engine. */
    public static final String DEFAULT_META_MESSAGE_EVENT_NAME = "midiMeta";

    /** The MIDI message timestamp value of no timestamp ({@code -1}). */
    public static final long NO_TIME_STAMP = -1;

    public static final String TAG_SEQUENCER_CONNECTED_TO_SYNTHESIZER = "sequencerConnectedToSynthesizer";

    public static final String TAG_LOAD_ALL_INSTRUMENTS = "loadAllInstruments";

    public static final String TAG_MIDI_SHORT_MESSAGE_EVENT_NAME = "midiShortMessageEventName";

    public static final String TAG_MIDI_SYSEX_MESSAGE_EVENT_NAME = "midiSysexMessageEventName";

    public static final String TAG_MIDI_META_MESSAGE_EVENT_NAME = "midiMetaMessageEventName";

    private MidiConstants() {
        //
    }
}
