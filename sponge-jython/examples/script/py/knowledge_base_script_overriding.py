"""
Sponge Knowledge base
Script - Overriding
Note that auto-enable is turned off in the configuration.
"""
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("receivedEventA1", AtomicInteger(0))
    EPS.setVariable("receivedEventA2", AtomicInteger(0))
    EPS.setVariable("functionA1", AtomicInteger(0))
    EPS.setVariable("functionA2", AtomicInteger(0))

class TriggerA(Trigger):
    def configure(self):
        self.event = "a"
    def run(self, event):
        EPS.getVariable("receivedEventA1").set(1)

# Execute immediately while loading
EPS.enable(TriggerA)

class TriggerA(Trigger):
    def configure(self):
        self.event = "a"
    def run(self, event):
        EPS.getVariable("receivedEventA2").set(2)

# Execute immediately while loading
EPS.enable(TriggerA)

def onStartup():
    EPS.event("a").send()
    functionA()

def functionA():
    EPS.getVariable("functionA1").set(1)

def functionA():
    EPS.getVariable("functionA2").set(2)

