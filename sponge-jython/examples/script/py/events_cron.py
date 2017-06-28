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
    def configure(self):
        self.eventName = "cronEvent"
    def run(self, event):
        global eventCounter
        self.logger.debug("Received event {}: {}", eventCounter.get() + 1, event.name)
        if eventCounter.get() + 1 == 2:
            self.logger.debug("removing scheduled event")
            EPS.removeEvent(scheduleEntry)
        eventCounter.incrementAndGet()

def onStartup():
    global scheduleEntry
    # send event every second
    scheduleEntry = EPS.event("cronEvent").sendAt("0/1 * * * * ?")
