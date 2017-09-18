"""
Sponge Knowledge base
Unordered rules - instances
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("countAB", AtomicInteger(0))
    EPS.setVariable("countA", AtomicInteger(0))
    EPS.setVariable("max", 100)

class AB(Rule):
    def onConfigure(self):
        self.events = ["a", "b"]; self.ordered = False
    def onRun(self, event):
        EPS.getVariable("countAB").incrementAndGet()

class A(Rule):
    def onConfigure(self):
        self.events = ["a a1", "a a2"]; self.ordered = False
    def onRun(self, event):
        EPS.getVariable("countA").incrementAndGet()

def onStartup():
    for i in range(EPS.getVariable("max")):
        EPS.event("a").send()
        EPS.event("b").send()
