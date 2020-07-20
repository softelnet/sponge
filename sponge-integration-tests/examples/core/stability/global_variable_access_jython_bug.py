"""
Sponge Knowledge Base
Global variable access Jython 2.7.1 bug: http://bugs.jython.org/issue2487
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("test1", AtomicBoolean(False))
    sponge.setVariable("test2", AtomicBoolean(False))
    sponge.setVariable("stopped", AtomicBoolean(False))

class AbstractE(Trigger):
    def onConfigure(self):
        self.withEvent("e")

class E1(AbstractE):
    def onRun(self, event):
        sponge.getVariable("test1").set(True)
        sponge.getVariable("test2").set(True)

class E2(AbstractE):
    def onRun(self, event):
        sponge.getVariable("test1").set(False)
        sponge.getVariable("test2").set(False)

class E3(AbstractE):
    def onRun(self, event):
        sponge.getVariable("test1").set(True)
        sponge.getVariable("test2").set(True)

class E4(AbstractE):
    def onRun(self, event):
        sponge.getVariable("test1").set(False)
        sponge.getVariable("test2").set(False)

class Stop(Trigger):
    def onConfigure(self):
        self.withEvent("stop")
    def onRun(self, event):
        sponge.getVariable("stopped").set(True)

def onStartup():
    sponge.event("e").sendAfter(0, 1)
    sponge.event("e").sendAfter(0, 1)
    sponge.event("e").sendAfter(0, 1)
    sponge.event("e").sendAfter(0, 1)
    sponge.event("stop").sendAfter(Duration.ofMinutes(15))
