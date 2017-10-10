"""
Sponge Knowledge base
Reload - impact on duration of rules.
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger

class ReloadTrigger(Trigger):
    def onConfigure(self):
        self.event = "reload"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        EPS.requestReload()

class RuleAShouldNoBeRun(Rule):
    def onConfigure(self):
        self.events = ["e1", "e2 :last"]
        self.duration = Duration.ofSeconds(3)
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleAFired").set(True)

class RuleBShouldNotBeRun(Rule):
    def onConfigure(self):
        self.events = ["e1", "e3 :last"]
        self.duration = Duration.ofSeconds(3)
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleBFired").set(True)

class RuleCShouldBeRun(Rule):
    def onConfigure(self):
        self.events = ["e1", "e2"]
        self.duration = Duration.ofSeconds(3)
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("ruleCFired").set(True)

class EndTestTrigger(Trigger):
    def onConfigure(self):
        self.event = "endTest"
    def onRun(self, event):
        EPS.getVariable("endTest").set(True)

def onInit():
    # Variables for assertions only
    EPS.setVariable("ruleAFired", AtomicBoolean(False))
    EPS.setVariable("ruleBFired", AtomicBoolean(False))
    EPS.setVariable("ruleCFired", AtomicBoolean(False))
    EPS.setVariable("endTest", AtomicBoolean(False))

def onStartup():
    EPS.event("reload").sendAfter(1000)
    EPS.event("e1").send()
    EPS.event("e2").send()
    EPS.event("e3").sendAfter(2000)
    EPS.event("endTest").sendAfter(5000)

def onBeforeReload():
    EPS.logger.debug("onBeforeReload")

def onAfterReload():
    EPS.logger.debug("onAfterReload")
