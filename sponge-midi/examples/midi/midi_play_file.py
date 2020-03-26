"""
Sponge Knowledge base
MIDI play file
"""

from javax.sound.midi import ShortMessage
from org.openksavi.sponge.midi import MidiUtils
from java.io import File

class SameSound(Trigger):
    def onConfigure(self):
        self.withEvent("midiShort")
    def onRun(self, event):
        # Exact sound as read from the MIDi file.
        midi.sound(event.message)
        # Add some drums
        if event.data1 < 50:
            midi.sound(event.command, 9, event.data1, event.data2)

class Log(Trigger):
    def onConfigure(self):
        self.withEvent("midi.*")
    def onRun(self, event):
        self.logger.info("{}MIDI message: {}", "[" + MidiUtils.getKeyNote(event.data1) + "] " if event.midiCategory.code == "short"
                         and event.command == ShortMessage.NOTE_ON else "", event.messageString)

class Stop(Trigger):
    def onConfigure(self):
        self.withEvent("midiMeta")
    def onRun(self, event):
        # Playback finished
        if event.messageType == 47:
            sponge.requestShutdown()

def onStartup():
    sponge.logger.info("This example program enables a user to play 60 seconds of a MIDI file using the Sponge MIDI plugin.")
    sponge.logger.info("The sample, Public Domain licensed, MIDI file has been downloaded from The Mutopia Project (http://www.ibiblio.org/mutopia/cgibin/piece-info.cgi?id=743)")
    sponge.logger.info("The source location is http://www.ibiblio.org/mutopia/ftp/ChopinFF/O10/op-10-12-wfi/op-10-12-wfi.mid")
    midi.startPlay(File("examples/midi/op-10-12-wfi.mid"))
    sponge.logger.info("Type: {}, resolution: {}, tempo: {}", midi.sequencer.sequence.divisionType, midi.sequencer.sequence.resolution, midi.sequencer.tempoInBPM)
