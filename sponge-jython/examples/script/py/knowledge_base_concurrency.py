"""
Sponge Knowledge Base
Concurrency
"""

from java.util.concurrent.atomic import AtomicReference
from java.util.concurrent import TimeUnit

def onInit():
    # Variables for assertions only
    sponge.setVariable("value", AtomicReference(""))

class A(Trigger):
    def onConfigure(self):
        self.withEvent("a")
    def onRun(self, event):
        TimeUnit.SECONDS.sleep(1)
        sponge.getVariable("value").set("A1")
        TimeUnit.SECONDS.sleep(3)
        sponge.getVariable("value").set("A2")

class B(Trigger):
    def onConfigure(self):
        self.withEvent("b")
    def onRun(self, event):
        TimeUnit.SECONDS.sleep(2)
        sponge.getVariable("value").set("B1")
        TimeUnit.SECONDS.sleep(4)
        sponge.getVariable("value").set("B2")

class C(Trigger):
    def onConfigure(self):
        self.withEvent("c")
    def onRun(self, event):
        TimeUnit.SECONDS.sleep(8)
        sponge.getVariable("value").set("C1")
        TimeUnit.SECONDS.sleep(1)
        sponge.getVariable("value").set("C2")

def onStartup():
    sponge.event("a").send()
    sponge.event("b").send()
    sponge.event("c").send()
