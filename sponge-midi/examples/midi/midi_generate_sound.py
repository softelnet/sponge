"""
Sponge Knowledge base
MIDI generate sound
"""

from javax.sound.midi import ShortMessage
from org.openksavi.sponge.midi import MidiUtils

class SameSound(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        midi.sound(event.message)

class Log(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        self.logger.info("{}Input message: {}", "[" + MidiUtils.getKeyNote(event.data1) + "] " if event.command == ShortMessage.NOTE_ON else "",
                         event.messageString)

class Stop(Trigger):
    def onConfigure(self):
        self.withEvent("exit")
    def onRun(self, event):
        sponge.requestShutdown()

def onStartup():
    sponge.logger.info("This example program generates simple MIDI sounds using the Sponge MIDI plugin.")
    midi.setInstrument(0, "Violin")
    max = 10
    for i in range(max):
        sponge.event(midi.createShortMessageEvent(midi.createShortMessage(ShortMessage.NOTE_ON, 0, 60 + i, 80))).sendAfter(Duration.ofSeconds(i))
        sponge.event(midi.createShortMessageEvent(midi.createShortMessage(ShortMessage.NOTE_OFF, 0, 60 + i, 80))).sendAfter(Duration.ofSeconds(i+1))
    sponge.event("exit").sendAfter(Duration.ofSeconds(max + 1))
