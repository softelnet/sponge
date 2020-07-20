"""
Sponge Knowledge Base
Simple trigger
"""

class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        print "Sound the alarm!"
