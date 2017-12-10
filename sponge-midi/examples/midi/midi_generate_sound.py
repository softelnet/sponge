"""
Sponge Knowledge base
MIDI generate sound
"""

from javax.sound.midi import ShortMessage
from org.openksavi.sponge.midi import MidiUtils

class SameSound(Trigger):
    def onConfigure(self):
        self.event = "midiShort"
    def onRun(self, event):
        midi.sound(event.message)

class Log(Trigger):
    def onConfigure(self):
        self.event = "midiShort"
    def onRun(self, event):
        self.logger.info("{}Input message: {}", "[" + MidiUtils.getKeyNote(event.data1) + "] " if event.command == ShortMessage.NOTE_ON else "",
                         event.messageString)

class Stop(Trigger):
    def onConfigure(self):
        self.event = "exit"
    def onRun(self, event):
        EPS.requestShutdown()

def onStartup():
    EPS.logger.info("This example program generates simple MIDI sounds using the Sponge MIDI plugin.")
    midi.setInstrument(0, "Violin")
    max = 10
    for i in range(max):
        EPS.event(midi.createShortMessageEvent(midi.createShortMessage(ShortMessage.NOTE_ON, 0, 60 + i, 80))).sendAfter(Duration.ofSeconds(i))
        EPS.event(midi.createShortMessageEvent(midi.createShortMessage(ShortMessage.NOTE_OFF, 0, 60 + i, 80))).sendAfter(Duration.ofSeconds(i+1))
    EPS.event("exit").sendAfter(Duration.ofSeconds(max + 1))
