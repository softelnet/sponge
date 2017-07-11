"""
Sponge Knowledge base
Concurrency
"""

from java.util.concurrent.atomic import AtomicReference
from java.util.concurrent import TimeUnit

def onInit():
    # Variables for assertions only
    EPS.setVariable("value", AtomicReference(""))

class A(Trigger):
    def configure(self):
        self.event = "a"
    def run(self, event):
        TimeUnit.SECONDS.sleep(1)
        EPS.getVariable("value").set("A1")
        TimeUnit.SECONDS.sleep(3)
        EPS.getVariable("value").set("A2")

class B(Trigger):
    def configure(self):
        self.event = "b"
    def run(self, event):
        TimeUnit.SECONDS.sleep(2)
        EPS.getVariable("value").set("B1")
        TimeUnit.SECONDS.sleep(4)
        EPS.getVariable("value").set("B2")

class C(Trigger):
    def configure(self):
        self.event = "c"
    def run(self, event):
        TimeUnit.SECONDS.sleep(8)
        EPS.getVariable("value").set("C1")
        TimeUnit.SECONDS.sleep(1)
        EPS.getVariable("value").set("C2")

def onStartup():
    EPS.event("a").send()
    EPS.event("b").send()
    EPS.event("c").send()
