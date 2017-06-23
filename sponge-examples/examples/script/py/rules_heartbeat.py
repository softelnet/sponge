"""
Sponge Knowledge base
Heartbeat
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    global hearbeatEventEntry
    hearbeatEventEntry = None
    EPS.setVariable("soundTheAlarm", AtomicBoolean(False))

class HeartbeatFilter(Filter):
    def configure(self):
        self.eventName = "heartbeat"
    def init(self):
        self.heartbeatCounter = 0
    def accepts(self, event):
        self.heartbeatCounter += 1
        if self.heartbeatCounter > 2:
            global hearbeatEventEntry
            EPS.removeEvent(hearbeatEventEntry)
            return False
        else:
            return True

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule(Rule):
    def configure(self):
        self.events = ["heartbeat h1", "heartbeat h2 :none"]
        self.duration = Duration.ofSeconds(2)
    def run(self, event):
        EPS.event("alarm").set("severity", 1).send()

class AlarmTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        print "Sound the alarm!"
        EPS.getVariable("soundTheAlarm").set(True)

def onStartup():
    global hearbeatEventEntry
    hearbeatEventEntry = EPS.event("heartbeat").sendAfter(100, 1000)
