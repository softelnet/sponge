"""
Sponge Knowledge Base
Event pattern cache
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("count", AtomicInteger(0))

class A(Trigger):
    def onConfigure(self):
        self.withEvent("a.+")
    def onRun(self, event):
        self.logger.debug("Received {}", event.name)
        sponge.getVariable("count").incrementAndGet()

class PhaseTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("phase.+")
    def onRun(self, event):
        if event.name == "phase1":
            self.sendEvents()
        elif event.name == "phase2":
            sponge.disable(A)
            self.sendEvents()
        elif event.name == "phase3":
            sponge.enable(A)
            self.sendEvents()
    def sendEvents(self):
        for name in ["a1", "b1", "a2", "b2", "a", "b", "a1", "b2"]:
            sponge.event(name).send()

def onStartup():
    sponge.event("phase1").send()
    sponge.event("phase2").sendAfter(Duration.ofSeconds(2))
    sponge.event("phase3").sendAfter(Duration.ofSeconds(4))
