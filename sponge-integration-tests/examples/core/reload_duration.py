"""
Sponge Knowledge base
Reload - impact on duration of rules.
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger
from org.openksavi.sponge.test import TestStatus

class ReloadTrigger(Trigger):
    def configure(self):
        self.eventName = "reload"
    def run(self, event):
        self.logger.debug("Received event: {}", event.name)
        EPS.requestReload()

class RuleAShouldBeRun(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last"]
        self.duration = Duration.ofSeconds(3)
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleAFired").set(True)

class RuleBShouldNotBeRun(Rule):
    def configure(self):
        self.events = ["e1", "e3 :last"]
        self.duration = Duration.ofSeconds(3)
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleBFired").set(True)

class RuleCShouldBeRun(Rule):
    def configure(self):
        self.events = ["e1", "e2"]
        self.duration = Duration.ofSeconds(3)
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleCFired").set(True)

def onInit():
    # Variables for assertions only
    EPS.setVariable("ruleAFired", AtomicBoolean(False))
    EPS.setVariable("ruleBFired", AtomicBoolean(False))
    EPS.setVariable("ruleCFired", AtomicBoolean(False))

def onStartup():
    EPS.event("reload").sendAfter(1000)
    EPS.event("e1").sendAfter(100)
    EPS.event("e2").sendAfter(200)
    EPS.event("e3").sendAfter(2000)

def onBeforeReload():
    LOGGER.debug("onBeforeReload")

def onAfterReload():
    LOGGER.debug("onAfterReload")
