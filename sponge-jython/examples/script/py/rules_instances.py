"""
Sponge Knowledge base
Rules - instances
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("countA", AtomicInteger(0))
    EPS.setVariable("countB", AtomicInteger(0))
    EPS.setVariable("max", 100)

class RuleA(Rule):
    def onConfigure(self):
        self.events = ["a a1", "a a2"]
    def onRun(self, event):
        EPS.getVariable("countA").incrementAndGet()

class RuleB(Rule):
    def onConfigure(self):
        self.events = ["b b1", "b b2"]
    def onRun(self, event):
        EPS.getVariable("countB").incrementAndGet()

def onStartup():
    for i in range(EPS.getVariable("max")):
        EPS.event("a").send()
        EPS.event("b").send()
