"""
Sponge Knowledge Base
Heartbeat
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    global hearbeatEventEntry
    hearbeatEventEntry = None
    sponge.setVariable("soundTheAlarm", AtomicBoolean(False))

class HeartbeatFilter(Filter):
    def onConfigure(self):
        self.withEvent("heartbeat")
    def onInit(self):
        self.heartbeatCounter = 0
    def onAccept(self, event):
        self.heartbeatCounter += 1
        if self.heartbeatCounter > 2:
            global hearbeatEventEntry
            sponge.removeEvent(hearbeatEventEntry)
            return False
        else:
            return True

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule(Rule):
    def onConfigure(self):
        self.withEvents(["heartbeat h1", "heartbeat h2 :none"])
        self.withCondition("h2", lambda rule, event: rule.firstEvent.get("source") == event.get("source"))
        self.withDuration(Duration.ofSeconds(2))
    def onRun(self, event):
        sponge.event("alarm").set("severity", 1).send()

class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        print "Sound the alarm!"
        sponge.getVariable("soundTheAlarm").set(True)

def onStartup():
    global hearbeatEventEntry
    hearbeatEventEntry = sponge.event("heartbeat").set("source", "Host1").sendAfter(100, 1000)
