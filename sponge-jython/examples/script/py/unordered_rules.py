"""
Sponge Knowledge base
Using unordered rules
"""

from java.util.concurrent.atomic import AtomicInteger
from org.openksavi.sponge.examples import SameSourceJavaUnorderedRule
from org.openksavi.sponge.core.library import Deduplication

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", AtomicInteger(0))


class FirstRule(Rule):
    def onConfigure(self):
        self.events = ["filesystemFailure", "diskFailure"]; self.ordered = False
        self.addAllConditions(lambda rule, event: rule.firstEvent.get("source") == event.get("source"))
        self.addAllConditions(lambda rule, event:
                           Duration.between(rule.firstEvent.time, event.time).seconds <= 2)
        self.duration = Duration.ofSeconds(5)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventSequence)
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
        sponge.event("alarm").set("source", self.firstEvent.get("source")).send()

class SameSourceAllRule(Rule):
    def onConfigure(self):
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"]; self.ordered = False
        self.addConditions("e1", self.severityCondition)
        self.addConditions("e2", self.severityCondition, self.diskFailureSourceCondition)
        self.duration = Duration.ofSeconds(5)
    def onRun(self, event):
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                         self.eventSequence)
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def severityCondition(self, event):
        return int(event.get("severity")) > 5
    def diskFailureSourceCondition(self, event):
        # Both events have to have the same source
        return event.get("source") == self.firstEvent.get("source") and \
            Duration.between(self.firstEvent.time, event.time).seconds <= 4

class AlarmFilter(Filter):
    def onConfigure(self):
        self.event = "alarm"
    def onInit(self):
        self.deduplication = Deduplication("source")
        self.deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit.SECONDS)
    def onAccept(self, event):
        return self.deduplication.onAccept(event)

class Alarm(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        self.logger.debug("Received alarm from {}", event.get("source"))

def onLoad():
    sponge.enableJava(SameSourceJavaUnorderedRule)

def onStartup():
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 6).set("source", "server1").send()

