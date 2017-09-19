"""
Sponge Knowledge base
Multiple knowledge base files
"""

class SoundTrigger(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        print "Sound the alarm (severity: " + str(event.get("severity")) + ")!"
