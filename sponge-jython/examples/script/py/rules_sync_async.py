"""
Sponge Knowledge base
Using rules - synchronous and asynchronous
"""

from org.openksavi.sponge.test.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    sponge.setVariable("correlationEventsLog", correlationEventsLog)

class RuleFFF(Rule):
    def onConfigure(self):
        self.events = ["e1", "e2", "e3 :first"]
        self.synchronous = True
    def onRun(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFF", self)

class RuleFFL(Rule):
    def onConfigure(self):
        self.events = ["e1", "e2", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofSeconds(2)
        self.synchronous = False
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
