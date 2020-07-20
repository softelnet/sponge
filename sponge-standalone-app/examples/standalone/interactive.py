"""
Sponge Knowledge Base
Interactive mode example
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("alarms", AtomicInteger(0))
    sponge.setVariable("notifications", AtomicInteger(0))

class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        sponge.getVariable("alarms").incrementAndGet()
        print "Sound the alarm!"

