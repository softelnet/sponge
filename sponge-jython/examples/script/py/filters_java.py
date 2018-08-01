"""
Sponge Knowledge base
Using java filters 
"""

from java.util import Collections, HashMap
from java.util.concurrent.atomic import AtomicInteger
from org.openksavi.sponge.examples import ShapeFilter

def onInit():
    global eventCounter
    # Variables for assertions only
    eventCounter = Collections.synchronizedMap(HashMap())
    eventCounter.put("e1", AtomicInteger(0))
    eventCounter.put("e2", AtomicInteger(0))
    eventCounter.put("e3", AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)

class FilterTrigger(Trigger):
    def onConfigure(self):
        self.setEvents("e1", "e2", "e3")
    def onRun(self, event):
        self.logger.debug("Processing trigger for event {}", event)
        global eventCounter
        eventCounter.get(event.name).incrementAndGet()

def onLoad():
    sponge.enableJava(ShapeFilter)


def onStartup():
    sponge.event("e1").sendAfter(100, 100)
    sponge.event("e2").set("shape", "square").sendAfter(200, 100)
    sponge.event("e3").set("shape", "circle").sendAfter(300, 100)
