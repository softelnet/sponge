"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    sponge.setVariable("trigger", AtomicBoolean(False))

class AssertTrigger(Trigger):
    def onConfigure(self):
        self.event = "e"
    def onRun(self, event):
        sponge.getVariable("trigger").set(True)

def onStartup():
    sponge.event("e").send()

