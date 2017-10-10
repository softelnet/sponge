"""
Sponge Knowledge base
Interactive mode example
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("alarms", AtomicInteger(0))
    EPS.setVariable("notifications", AtomicInteger(0))

class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        EPS.getVariable("alarms").incrementAndGet()
        print "Sound the alarm!"

