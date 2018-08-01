"""
Sponge Knowledge base
Using knowledge base manager: enabling/disabling processors.
Note that auto-enable is turned off in the configuration.
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("verifyTriggerEnabled", AtomicBoolean(False))
    sponge.setVariable("verifyTriggerDisabled", AtomicBoolean(False))
    sponge.setVariable("verificationDone", AtomicBoolean(False))

def verifyManager():
    triggerCount = sponge.engine.triggers.size()
    sponge.enable(TriggerA)
    sponge.enable(TriggerA)
    sponge.getVariable("verifyTriggerEnabled").set(sponge.engine.triggers.size() == triggerCount + 1)
    triggerCount = sponge.engine.triggers.size()
    sponge.disable(TriggerA)
    sponge.disable(TriggerA)
    sponge.getVariable("verifyTriggerDisabled").set(sponge.engine.triggers.size() == triggerCount - 1)
    sponge.getVariable("verificationDone").set(True)

class VerifyTrigger(Trigger):
    def onConfigure(self):
        self.event = "verify"
    def onRun(self, event):
        verifyManager()

class TriggerA(Trigger):
    def onConfigure(self):
        self.event = "a"
    def onRun(self, event):
        pass

def onLoad():
    sponge.enable(VerifyTrigger)

def onStartup():
    sponge.event("verify").send()
