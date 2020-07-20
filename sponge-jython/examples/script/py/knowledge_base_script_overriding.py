"""
Sponge Knowledge Base
Script - Overriding
Note that auto-enable is turned off in the configuration.
"""
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedEventA1", AtomicInteger(0))
    sponge.setVariable("receivedEventA2", AtomicInteger(0))
    sponge.setVariable("functionA1", AtomicInteger(0))
    sponge.setVariable("functionA2", AtomicInteger(0))

class TriggerA(Trigger):
    def onConfigure(self):
        self.withEvent("a")
    def onRun(self, event):
        sponge.getVariable("receivedEventA1").set(1)

# Execute immediately while loading
sponge.enable(TriggerA)

class TriggerA(Trigger):
    def onConfigure(self):
        self.withEvent("a")
    def onRun(self, event):
        sponge.getVariable("receivedEventA2").set(2)

# Execute immediately while loading
sponge.enable(TriggerA)

def onStartup():
    sponge.event("a").send()
    functionA()

def functionA():
    sponge.getVariable("functionA1").set(1)

def functionA():
    sponge.getVariable("functionA2").set(2)

