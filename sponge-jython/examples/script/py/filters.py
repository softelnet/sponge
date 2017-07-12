"""
Sponge Knowledge base
Using filters
"""

from java.util import Collections, HashMap
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    global eventCounter
    # Variables for assertions only
    eventCounter = Collections.synchronizedMap(HashMap())
    eventCounter.put("blue", AtomicInteger(0))
    eventCounter.put("red", AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)

class ColorFilter(Filter):
    def configure(self):
        self.event = "e1"
    def accepts(self, event):
        self.logger.debug("Received event {}", event)
        color = event.get("color")
        if (color is None or color != "blue"):
            self.logger.debug("rejected")
            return False
        else:
            self.logger.debug("accepted")
            return True

class ColorTrigger(Trigger):
    def configure(self):
        self.event = "e1"
    def run(self, event):
        self.logger.debug("Received event {}", event)
        global eventCounter
        eventCounter.get(event.get("color")).incrementAndGet()

def onStartup():
    EPS.event("e1").send()
    EPS.event("e1").set("color", "red").send()
    EPS.event("e1").set("color", "blue").send()








