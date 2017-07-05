"""
Sponge Knowledge base
Using rules - synchronous and asynchronous
"""

from org.openksavi.sponge.test.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

class RuleFFF(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :first"]
        self.synchronous = True
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFF", self)

class RuleFFL(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofMillis(100)
        self.synchronous = False
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFL", self)

def onStartup():
    EPS.event("e1").set("label", "1").sendAfter(1)
    EPS.event("e2").set("label", "2").sendAfter(2)
    EPS.event("e2").set("label", "3").sendAfter(3)
    EPS.event("e2").set("label", "4").sendAfter(4)
    EPS.event("e3").set("label", "5").sendAfter(5)
    EPS.event("e3").set("label", "6").sendAfter(6)
    EPS.event("e3").set("label", "7").sendAfter(7)