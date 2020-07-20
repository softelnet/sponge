"""
Sponge Knowledge Base
MIDI input
"""

from javax.sound.midi import ShortMessage
from org.openksavi.sponge.midi import MidiUtils

shift = 12

# Plays the exact MIDI message.
class SameSound(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        midi.sound(event.message)

# Plays (note on) the same note but in a different channel.
class MultiSound(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        if midi.channels[4] and event.command == ShortMessage.NOTE_ON:
            midi.channels[4].noteOn(event.message.data1, event.message.data2 / 2)

# Plays with a simple sound effect.
class EffectSound(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        global shift
        midi.sound(event.command, event.channel, event.data1 - shift if event.data1 - shift >= 0 else 0, event.data2 / 2)
        midi.sound(event.command, event.channel, event.data1 + shift if event.data1 + shift <= 127 else 127, event.data2 / 2)
        midi.sound(event.command, 9, event.data1 + shift if event.data1 + shift <= 127 else 127, event.data2 / 2)

class Log(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        self.logger.info("{}Input message: {}", "[" + MidiUtils.getKeyNote(event.data1) + "] " if event.command == ShortMessage.NOTE_ON else "",
                         event.messageString)

def onStartup():
    sponge.logger.info("This example program enables a user to play an input MIDI device (e.g. a MIDI keyboard) using the Sponge MIDI plugin.")
    midi.connectDefaultInputDevice()
    sponge.logger.info("Input MIDI device: {}", midi.inputDevice.deviceInfo.name)
    sponge.logger.info("Instruments: {}", ",".join(list(map(lambda i: i.name + " (" + str(i.patch.bank) + "/" + str(i.patch.program) + ")", midi.instruments))))
    midi.setInstrument(0, "Electric Piano 1")

    # Uncomment the following lines to disable additional sound effects.
    #sponge.disable(EffectSound)
    #sponge.disable(MultiSound)
