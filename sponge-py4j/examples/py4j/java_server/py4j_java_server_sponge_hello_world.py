"""
Sponge Knowledge base
Py4J Hello world
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("eventCounter", AtomicInteger(0))

class HelloWorldTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("helloEvent")
    def onRun(self, event):
        self.logger.info("Say: {}", event.get("say"))
        sponge.getVariable("eventCounter").incrementAndGet()
