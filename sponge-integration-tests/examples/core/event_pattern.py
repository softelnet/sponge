"""
Sponge Knowledge base
Event pattern cache
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("count", AtomicInteger(0))

class A(Trigger):
    def onConfigure(self):
        self.event = "a.+"
    def onRun(self, event):
        self.logger.debug("Received {}", event.name)
        EPS.getVariable("count").incrementAndGet()

class PhaseTrigger(Trigger):
    def onConfigure(self):
        self.event = "phase.+"
    def onRun(self, event):
        if event.name == "phase1":
            self.sendEvents()
        elif event.name == "phase2":
            EPS.disable(A)
            self.sendEvents()
        elif event.name == "phase3":
            EPS.enable(A)
            self.sendEvents()
    def sendEvents(self):
        for name in ["a1", "b1", "a2", "b2", "a", "b", "a1", "b2"]:
            EPS.event(name).send()

def onStartup():
    EPS.event("phase1").send()
    EPS.event("phase2").sendAfter(Duration.ofSeconds(2))
    EPS.event("phase3").sendAfter(Duration.ofSeconds(4))
