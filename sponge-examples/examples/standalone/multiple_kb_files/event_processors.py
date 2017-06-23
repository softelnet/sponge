"""
Sponge Knowledge base
Multiple knowledge base files
"""

class SoundTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        print "Sound the alarm (severity: " + str(event.get("severity")) + ")!"

class NotifyAdministratorTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        print "Notify administrator (alarm severity: " + str(event.get("severity")) + ")!"