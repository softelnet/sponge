"""
Sponge Knowledge base
Using rules - events
"""

from org.openksavi.sponge.test.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF(Rule):
    def onConfigure(self):
        self.events = ["e1", "e5 :none", "e6 :none", "e3"]
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNF", self)

class RuleFNNNL(Rule):
    def onConfigure(self):
        self.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNNL", self)

class RuleFNNNLReject(Rule):
    def onConfigure(self):
        self.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNNLRejected", self)

class RuleFNFNL(Rule):
    def onConfigure(self):
        self.events = ["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNFNL", self)

def onStartup():
    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
