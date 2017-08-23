"""
Sponge Knowledge base
Correlators - Event pattern
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("nameCount", AtomicInteger(0))
    EPS.setVariable("patternCount", AtomicInteger(0))

class NameCorrelator(Correlator):
    def onConfigure(self):
        self.events = ["a1", "b1"]
        self.maxInstances = 1
    def onEvent(self, event):
        EPS.getVariable("nameCount").incrementAndGet()

class PatternCorrelator(Correlator):
    def onConfigure(self):
        self.events = ["a.+", "b.*"]
        self.maxInstances = 1
    def onEvent(self, event):
        EPS.getVariable("patternCount").incrementAndGet()

def onStartup():
    for name in ["a1", "b1", "a2", "b2", "a", "b", "a1", "b2"]:
        EPS.event(name).send()
