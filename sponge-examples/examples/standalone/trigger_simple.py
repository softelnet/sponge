"""
Sponge Knowledge base
Interactive mode example
"""

class AlarmTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        print "Sound the alarm!"
