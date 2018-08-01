"""
Sponge Knowledge base
System events
"""

from java.util.concurrent.atomic import AtomicInteger
from java.util import ArrayList

sponge.event("x").send()

def onInit():
    # Variables for assertions only
    sponge.setVariable("countA", AtomicInteger(0))
    sponge.setVariable("countB", AtomicInteger(0))
    sponge.setVariable("listC", ArrayList())

class A(Rule):
    def onConfigure(self):
        self.events = ["startup", "e :all"]
    def onRun(self, event):
        sponge.getVariable("countA").incrementAndGet()

class B(Rule):
    def onConfigure(self):
        self.events = ["startup", "a :none"]
        self.duration = Duration.ofSeconds(3)
    def onRun(self, event):
        sponge.getVariable("countB").incrementAndGet()

class C(Correlator):
    def onConfigure(self):
        self.events = [".*"]
        self.maxInstances = 1
    def onEvent(self, event):
        self.logger.debug(str(event))
        sponge.getVariable("listC").add(event.name)

def onStartup():
    for i in range(5):
        sponge.event("e").send()
