"""
Sponge Knowledge base
Using filters for deduplication of events.
"""

from java.util import Collections, HashMap
from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import TimeUnit

from org.openksavi.sponge.core.library import Deduplication

def onInit():
    global eventCounter
    # Variables for assertions only
    eventCounter = Collections.synchronizedMap(HashMap())
    eventCounter.put("e1-blue", AtomicInteger(0))
    eventCounter.put("e1-red", AtomicInteger(0))
    eventCounter.put("e2-blue", AtomicInteger(0))
    eventCounter.put("e2-red", AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)

class ColorDeduplicationFilter(Filter):
    def configure(self):
        self.event = "e1"
    def init(self):
        self.deduplication = Deduplication("color")
        self.deduplication.cacheBuilder.maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES)
    def accepts(self, event):
        return self.deduplication.accepts(event)

class ColorTrigger(Trigger):
    def configure(self):
        self.events = ["e1", "e2"]
    def run(self, event):
        self.logger.debug("Received event {}", event)
        global eventCounter
        eventCounter.get(event.name + "-" + event.get("color")).incrementAndGet()

def onStartup():
    EPS.event("e1").set("color", "red").sendAfter(100)
    EPS.event("e1").set("color", "blue").sendAfter(100)
    EPS.event("e2").set("color", "red").sendAfter(200)
    EPS.event("e2").set("color", "blue").sendAfter(200)

    EPS.event("e1").set("color", "red").sendAfter(300)
    EPS.event("e1").set("color", "blue").sendAfter(300)
    EPS.event("e2").set("color", "red").sendAfter(400)
    EPS.event("e2").set("color", "blue").sendAfter(400)
