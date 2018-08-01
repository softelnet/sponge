"""
Sponge Knowledge base
Unordered rules - instances
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("countAB", AtomicInteger(0))
    sponge.setVariable("countA", AtomicInteger(0))
    sponge.setVariable("max", 100)

class AB(Rule):
    def onConfigure(self):
        self.events = ["a", "b"]; self.ordered = False
    def onRun(self, event):
        sponge.getVariable("countAB").incrementAndGet()

class A(Rule):
    def onConfigure(self):
        self.events = ["a a1", "a a2"]; self.ordered = False
    def onRun(self, event):
        sponge.getVariable("countA").incrementAndGet()

def onStartup():
    for i in range(sponge.getVariable("max")):
        sponge.event("a").send()
        sponge.event("b").send()
