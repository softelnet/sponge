"""
Sponge Knowledge Base
Multiple knowledge base files
"""

class SoundTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        print "Sound the alarm (severity: " + str(event.get("severity")) + ")!"
