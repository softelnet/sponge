"""
Sponge Knowledge base
Using rules
"""

from java.util.concurrent.atomic import AtomicInteger
from org.openksavi.sponge.examples import SameSourceJavaRule

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", AtomicInteger(0))


class FirstRule(Rule):
    def onConfigure(self):
        # Events specified without aliases
        self.events = ["filesystemFailure", "diskFailure"]
        self.addConditions("diskFailure", lambda rule, event:
                           Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0)
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()

class SameSourceAllRule(Rule):
    def onConfigure(self):
        # Events specified with aliases (e1 and e2)
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"]
        self.addConditions("e1", self.severityCondition)
        self.addConditions("e2", self.severityCondition, self.diskFailureSourceCondition)
        self.duration = Duration.ofSeconds(8)
    def onRun(self, event):
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          self.eventSequence)
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def severityCondition(self, event):
        return int(event.get("severity")) > 5
    def diskFailureSourceCondition(self, event):
        # Both events have to have the same source
        event1 = self.getEvent("e1")
        return event.get("source") == event1.get("source") and \
            Duration.between(event1.time, event.time).seconds <= 4

def onLoad():
    sponge.enableJava(SameSourceJavaRule)

def onStartup():
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
