"""
Sponge Knowledge base
Rules - instances
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("countA", AtomicInteger(0))
    sponge.setVariable("countB", AtomicInteger(0))
    sponge.setVariable("max", 100)

class RuleA(Rule):
    def onConfigure(self):
        self.events = ["a a1", "a a2"]
    def onRun(self, event):
        sponge.getVariable("countA").incrementAndGet()

class RuleB(Rule):
    def onConfigure(self):
        self.events = ["b b1", "b b2"]
    def onRun(self, event):
        sponge.getVariable("countB").incrementAndGet()

def onStartup():
    for i in range(sponge.getVariable("max")):
        sponge.event("a").send()
        sponge.event("b").send()
