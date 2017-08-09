"""
Sponge Knowledge base
Triggers - Generating events and using triggers
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger
from org.openksavi.sponge.examples import SampleJavaTrigger

def onInit():
    # Variables for assertions only
    EPS.setVariable("receivedEventA", AtomicBoolean(False))
    EPS.setVariable("receivedEventBCount", AtomicInteger(0))
    EPS.setVariable("receivedEventTestJavaCount", AtomicInteger(0))

class TriggerA(Trigger):
    def onConfigure(self):
        self.event = "a"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        EPS.getVariable("receivedEventA").set(True)

class TriggerB(Trigger):
    def onConfigure(self):
        self.event = "b"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        receivedEventBCount = EPS.getVariable("receivedEventBCount")
        if receivedEventBCount.get() == 0:
            self.logger.debug("Statistics: {}", EPS.statisticsSummary)
        receivedEventBCount.incrementAndGet()

def onLoad():
    EPS.enableJava(SampleJavaTrigger)

def onStartup():
    EPS.logger.debug("Startup {}, triggers: {}", EPS.description, EPS.engine.triggers)
    EPS.logger.debug("Knowledge base name: {}", EPS.kb.name)
    EPS.event("a").send()
    EPS.event("b").sendAfter(200, 200)
    EPS.event("testJava").send()

def onShutdown():
    EPS.logger.debug("Shutting down")
