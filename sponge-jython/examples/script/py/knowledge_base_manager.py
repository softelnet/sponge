"""
Sponge Knowledge base
Using knowledge base manager: enabling/disabling processors.
Note that auto-enable is turned off in the configuration.
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("verifyTriggerEnabled", AtomicBoolean(False))
    EPS.setVariable("verifyTriggerDisabled", AtomicBoolean(False))
    EPS.setVariable("verificationDone", AtomicBoolean(False))

def verifyManager():
    triggerCount = EPS.engine.triggers.size()
    EPS.enable(TriggerA)
    EPS.enable(TriggerA)
    EPS.getVariable("verifyTriggerEnabled").set(EPS.engine.triggers.size() == triggerCount + 1)
    triggerCount = EPS.engine.triggers.size()
    EPS.disable(TriggerA)
    EPS.disable(TriggerA)
    EPS.getVariable("verifyTriggerDisabled").set(EPS.engine.triggers.size() == triggerCount - 1)
    EPS.getVariable("verificationDone").set(True)

class VerifyTrigger(Trigger):
    def configure(self):
        self.event = "verify"
    def run(self, event):
        verifyManager()

class TriggerA(Trigger):
    def configure(self):
        self.event = "a"
    def run(self, event):
        pass

def onLoad():
    EPS.enable(VerifyTrigger)

def onStartup():
    EPS.event("verify").send()
