"""
Sponge Knowledge Base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    sponge.setVariable("trigger", AtomicBoolean(False))

class AssertTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("e")
    def onRun(self, event):
        sponge.getVariable("trigger").set(True)

def onStartup():
    sponge.event("e").send()

