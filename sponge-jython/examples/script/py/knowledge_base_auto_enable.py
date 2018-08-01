"""
Sponge Knowledge base
Auto-enable
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("counter", AtomicInteger(0))

class AutoAction(Action):
    def onCall(self):
        self.logger.debug("Running")
        sponge.getVariable("counter").incrementAndGet()
        return None

class AutoFilter(Filter):
    def onConfigure(self):
        self.event = "e1"
    def onAccept(self, event):
        self.logger.debug("Received event: {}", event.name)
        sponge.getVariable("counter").incrementAndGet()
        return True

class AutoTrigger(Trigger):
    def onConfigure(self):
        self.event = "e1"
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        sponge.getVariable("counter").incrementAndGet()

class AutoRule(Rule):
    def onConfigure(self):
        self.events = ["e1", "e2"]
    def onRun(self, event):
        self.logger.debug("Running for sequence: {}", self.eventSequence)
        sponge.getVariable("counter").incrementAndGet()

class AutoCorrelator(Correlator):
    def onConfigure(self):
        self.events = ["e1", "e2"]
    def onAcceptAsFirst(self, event):
        return event.name == "e1"
    def onEvent(self, event):
        self.logger.debug("Received event: {}", event.name)
        if event.name == "e2":
            sponge.getVariable("counter").incrementAndGet()
            self.finish()

def onStartup():
    sponge.call("AutoAction")
    sponge.event("e1").send()
    sponge.event("e2").send()
