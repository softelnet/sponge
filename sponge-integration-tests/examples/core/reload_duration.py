"""
Sponge Knowledge Base
Reload - impact on duration of rules.
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger

class ReloadTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("reload")
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        sponge.requestReload()

class RuleAShouldNoBeRun(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :last"]).withDuration(Duration.ofSeconds(3))
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("ruleAFired").set(True)

class RuleBShouldNotBeRun(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e3 :last"]).withDuration(Duration.ofSeconds(3))
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("ruleBFired").set(True)

class RuleCShouldBeRun(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2"]).withDuration(Duration.ofSeconds(3))
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("ruleCFired").set(True)

class EndTestTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("endTest")
    def onRun(self, event):
        sponge.getVariable("endTest").set(True)

def onInit():
    # Variables for assertions only
    sponge.setVariable("ruleAFired", AtomicBoolean(False))
    sponge.setVariable("ruleBFired", AtomicBoolean(False))
    sponge.setVariable("ruleCFired", AtomicBoolean(False))
    sponge.setVariable("endTest", AtomicBoolean(False))

def onStartup():
    sponge.event("reload").sendAfter(1000)
    sponge.event("e1").send()
    sponge.event("e2").send()
    sponge.event("e3").sendAfter(2000)
    sponge.event("endTest").sendAfter(5000)

def onBeforeReload():
    sponge.logger.debug("onBeforeReload")

def onAfterReload():
    sponge.logger.debug("onAfterReload")
