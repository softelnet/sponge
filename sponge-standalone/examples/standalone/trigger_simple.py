"""
Sponge Knowledge base
Simple trigger
"""

class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        print "Sound the alarm!"
