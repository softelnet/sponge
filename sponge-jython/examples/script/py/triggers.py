"""
Sponge Knowledge base
Triggers - Generating events and using triggers
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger
from org.openksavi.sponge.examples import SampleJavaTrigger

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedEventA", AtomicBoolean(False))
    sponge.setVariable("receivedEventBCount", AtomicInteger(0))
    sponge.setVariable("receivedEventTestJavaCount", AtomicInteger(0))

class TriggerA(Trigger):
    def onConfigure(self):
        self.event = "a"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        sponge.getVariable("receivedEventA").set(True)

class TriggerB(Trigger):
    def onConfigure(self):
        self.event = "b"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        receivedEventBCount = sponge.getVariable("receivedEventBCount")
        if receivedEventBCount.get() == 0:
            self.logger.debug("Statistics: {}", sponge.statisticsSummary)
        receivedEventBCount.incrementAndGet()

def onLoad():
    sponge.enableJava(SampleJavaTrigger)

def onStartup():
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name)
    sponge.event("a").send()
    sponge.event("b").sendAfter(200, 200)
    sponge.event("testJava").send()

def onShutdown():
    sponge.logger.debug("Shutting down")
