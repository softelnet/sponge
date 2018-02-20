"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    EPS.setVariable("onRun", AtomicBoolean(False))
    EPS.setVariable("trigger", AtomicBoolean(False))

class AssertTrigger(Trigger):
    def onConfigure(self):
        self.event = "e"
    def onRun(self, event):
        EPS.getVariable("trigger").set(True)

def onStartup():
    EPS.event("e").send()

def onRun():
    EPS.getVariable("onRun").set(True)
    return True # Endless loop mode
