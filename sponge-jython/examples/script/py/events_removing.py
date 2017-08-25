"""
Sponge Knowledge base
Removing scheduled events
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    global eventEntry, eventCounter
    eventEntry = None
    eventCounter = AtomicInteger(0)
    EPS.setVariable("eventCounter", eventCounter)
    EPS.setVariable("allowNumber", 2)

class Trigger1(Trigger):
    def onConfigure(self):
        self.event = "e1"
    def onRun(self, event):
        global eventCounter
        eventCounter.incrementAndGet()
        self.logger.debug("Received event {}, counter: {}", event.name, eventCounter)
        if eventCounter.get() > EPS.getVariable("allowNumber"):
        	   self.logger.debug("This line should not be displayed!")

class Trigger2(Trigger):
    def onConfigure(self):
        self.event = "e2"
    def onRun(self, event):
        self.logger.debug("Removing entry")
        global eventEntry
        EPS.removeEvent(eventEntry)

def onStartup():
    global eventEntry
    start = 500
    interval = 1000
    eventEntry = EPS.event("e1").sendAfter(start, interval)
    EPS.event("e2").sendAfter(interval * EPS.getVariable("allowNumber"))
