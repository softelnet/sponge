"""
Sponge Knowledge base
Auto-enable
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("counter", AtomicInteger(0))

class AutoAction(Action):
    def run(self, args):
        self.logger.debug("Running")
        EPS.getVariable("counter").incrementAndGet()
        return args

class AutoFilter(Filter):
    def configure(self):
        self.eventName = "e1"
    def accepts(self, event):
        self.logger.debug("Received event: {}", event.name)
        EPS.getVariable("counter").incrementAndGet()
        return True

class AutoTrigger(Trigger):
    def configure(self):
        self.eventName = "e1"
    def run(self, event):
        self.logger.debug("Received event: {}", event.name)
        EPS.getVariable("counter").incrementAndGet()

class AutoRule(Rule):
    def configure(self):
        self.events = ["e1", "e2"]
    def run(self, event):
        self.logger.debug("Running for sequence: {}", self.eventSequence)
        EPS.getVariable("counter").incrementAndGet()

class AutoCorrelator(Correlator):
    def configure(self):
        self.eventNames = ["e1", "e2"]
    def acceptsAsFirst(self, event):
        return event.name == "e1"
    def onEvent(self, event):
        self.logger.debug("Received event: {}", event.name)
        if event.name == "e2":
            EPS.getVariable("counter").incrementAndGet()
            self.finish()

def onStartup():
    EPS.callAction("AutoAction")
    EPS.event("e1").send()
    EPS.event("e2").send()
