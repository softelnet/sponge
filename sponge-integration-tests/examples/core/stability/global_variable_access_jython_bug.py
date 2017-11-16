"""
Sponge Knowledge base
Global variable access Jython 2.7.1 bug: http://bugs.jython.org/issue2487
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("test1", AtomicBoolean(False))
    EPS.setVariable("test2", AtomicBoolean(False))
    EPS.setVariable("stopped", AtomicBoolean(False))

class AbstractE(Trigger):
    def onConfigure(self):
        self.event = "e"

class E1(AbstractE):
    def onRun(self, event):
        EPS.getVariable("test1").set(True)
        EPS.getVariable("test2").set(True)

class E2(AbstractE):
    def onRun(self, event):
        EPS.getVariable("test1").set(False)
        EPS.getVariable("test2").set(False)

class E3(AbstractE):
    def onRun(self, event):
        EPS.getVariable("test1").set(True)
        EPS.getVariable("test2").set(True)

class E4(AbstractE):
    def onRun(self, event):
        EPS.getVariable("test1").set(False)
        EPS.getVariable("test2").set(False)

class Stop(Trigger):
    def onConfigure(self):
        self.event = "stop"
    def onRun(self, event):
        EPS.getVariable("stopped").set(True)

def onStartup():
    EPS.event("e").sendAfter(0, 1)
    EPS.event("e").sendAfter(0, 1)
    EPS.event("e").sendAfter(0, 1)
    EPS.event("e").sendAfter(0, 1)
    EPS.event("stop").sendAfter(Duration.ofMinutes(15))
