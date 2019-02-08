"""
Sponge Knowledge base
Heartbeat 2
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    sponge.setVariable("soundTheAlarm", AtomicBoolean(False))

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule(Rule):
    def onConfigure(self):
        self.withEvents(["heartbeat h1", "heartbeat h2 :none"]).withDuration(Duration.ofSeconds(2))
    def onRun(self, event):
        self.logger.info("Sound the alarm!")
        sponge.getVariable("soundTheAlarm").set(True)

def onStartup():
    sponge.event("heartbeat").send()
    sponge.event("heartbeat").sendAfter(1000)
