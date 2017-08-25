"""
Sponge Knowledge base
Generating events by Cron
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    global scheduleEntry, eventCounter
    scheduleEntry = None
    eventCounter = AtomicInteger(0)
    EPS.setVariable("eventCounter", eventCounter)

class CronTrigger(Trigger):
    def onConfigure(self):
        self.event = "cronEvent"
    def onRun(self, event):
        global eventCounter
        eventCounter.incrementAndGet()
        self.logger.debug("Received event {}: {}", eventCounter.get(), event.name)
        if eventCounter.get() == 2:
            self.logger.debug("removing scheduled event")
            EPS.removeEvent(scheduleEntry)

def onStartup():
    global scheduleEntry
    # send event every 2 seconds
    scheduleEntry = EPS.event("cronEvent").sendAt("0/2 * * * * ?")
