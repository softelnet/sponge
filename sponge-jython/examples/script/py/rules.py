"""
Sponge Knowledge base
Using rules
"""

from java.util.concurrent.atomic import AtomicInteger
from org.openksavi.sponge.examples import SameSourceJavaRule

def onInit():
    # Variables for assertions only
    EPS.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    EPS.setVariable("sameSourceFirstFireCount", AtomicInteger(0))


class FirstRule(Rule):
    def configure(self):
        # Events specified without aliases
        self.events = ["filesystemFailure", "diskFailure"]
        self.setConditions("diskFailure", lambda rule, event:
                           Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0)
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("sameSourceFirstFireCount").incrementAndGet()

class SameSourceAllRule(Rule):
    def configure(self):
        # Events specified with aliases (e1 and e2)
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"]
        self.setConditions("e1", self.severityCondition)
        self.setConditions("e2", self.severityCondition, self.diskFailureSourceCondition)
        self.duration = Duration.ofSeconds(8)
    def run(self, event):
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          self.eventSequence)
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def severityCondition(self, event):
        return int(event.get("severity")) > 5
    def diskFailureSourceCondition(self, event):
        # Both events have to have the same source
        event1 = self.getEvent("e1")
        return event.get("source") == event1.get("source") and \
            Duration.between(event1.time, event.time).seconds <= 4

def onLoad():
    EPS.enableJava(SameSourceJavaRule)

def onStartup():
    EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server2").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 1).set("source", "server1").send()
