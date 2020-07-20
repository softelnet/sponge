"""
Sponge Knowledge Base
Synchronous and asynchronous event set processors
"""

from org.openksavi.sponge.examples.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    sponge.setVariable("correlationEventsLog", correlationEventsLog)

class RuleFFF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e3 :first"]).withSynchronous(True)
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFF", self)

class RuleFFL(Rule):
    def onConfigure(self):
        global defaultDuration
        self.withEvents(["e1", "e2", "e3 :last"]).withSynchronous(False).withDuration(Duration.ofMillis(1000))
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFL", self)

def onStartup():
    sponge.event("e1").set("label", "1").send()
    sponge.event("e2").set("label", "2").send()
    sponge.event("e2").set("label", "3").send()
    sponge.event("e2").set("label", "4").send()
    sponge.event("e3").set("label", "5").send()
    sponge.event("e3").set("label", "6").send()
    sponge.event("e3").set("label", "7").send()
