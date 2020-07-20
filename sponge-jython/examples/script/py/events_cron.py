"""
Sponge Knowledge Base
Generating events by Cron
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    global scheduleEntry, eventCounter
    scheduleEntry = None
    eventCounter = AtomicInteger(0)
    sponge.setVariable("eventCounter", eventCounter)

class CronTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("cronEvent")
    def onRun(self, event):
        global eventCounter
        eventCounter.incrementAndGet()
        self.logger.debug("Received event {}: {}", eventCounter.get(), event.name)
        if eventCounter.get() == 2:
            self.logger.debug("removing scheduled event")
            sponge.removeEvent(scheduleEntry)

def onStartup():
    global scheduleEntry
    # send event every 2 seconds
    scheduleEntry = sponge.event("cronEvent").sendAt("0/2 * * * * ?")
