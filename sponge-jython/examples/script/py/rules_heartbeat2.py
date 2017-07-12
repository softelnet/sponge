"""
Sponge Knowledge base
Heartbeat 2
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    EPS.setVariable("soundTheAlarm", AtomicBoolean(False))

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule(Rule):
    def configure(self):
        self.events = ["heartbeat h1", "heartbeat h2 :none"]
        self.duration = Duration.ofSeconds(2)
    def run(self, event):
        self.logger.info("Sound the alarm!")
        EPS.getVariable("soundTheAlarm").set(True)

def onStartup():
    EPS.event("heartbeat").send()
    EPS.event("heartbeat").sendAfter(1000)
