"""
Sponge Knowledge base
Py4J Hello world
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("eventCounter", AtomicInteger(0))

class HelloWorldTrigger(Trigger):
    def onConfigure(self):
        self.event = "helloEvent"
    def onRun(self, event):
        self.logger.info("Say: {}", event.get("say"))
        EPS.getVariable("eventCounter").incrementAndGet()
