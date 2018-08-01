"""
Sponge Knowledge base
Filters - Event pattern
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("nameCount", AtomicInteger(0))
    sponge.setVariable("patternCount", AtomicInteger(0))
    sponge.setVariable("acceptedCount", AtomicInteger(0))
    sponge.setVariable("notAcceptedCount", AtomicInteger(0))

class NameFilter(Filter):
    def onConfigure(self):
        self.event = "a1"
    def onAccept(self, event):
        sponge.getVariable("nameCount").incrementAndGet()
        return True

class PatternFilter(Filter):
    def onConfigure(self):
        self.event = "a.+"
    def onAccept(self, event):
        sponge.getVariable("patternCount").incrementAndGet()
        return False

class AcceptedTrigger(Trigger):
    def onConfigure(self):
        self.event = ".+"
    def onRun(self, event):
        self.logger.info("accepted {}", event.name)
        if event.name != EventName.STARTUP:
            sponge.getVariable("acceptedCount").incrementAndGet()

class NotAcceptedTrigger(Trigger):
    def onConfigure(self):
        self.event = "a.+"
    def onRun(self, event):
        sponge.getVariable("notAcceptedCount").incrementAndGet()

def onStartup():
    for name in ["a1", "b1", "a2", "b2", "a", "b", "a1", "b2"]:
        sponge.event(name).send()
