"""
Sponge Knowledge base
Using rules - events
"""

from org.openksavi.sponge.core.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e6 :none", "e3"]
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNF", self)

class RuleFNNNL(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNNL", self)

class RuleFNNNLReject(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNNLRejected", self)

class RuleFNFNL(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNFNL", self)

def onStartup():
    EPS.event("e1").set("label", "1").sendAfter(100)
    EPS.event("e2").set("label", "2").sendAfter(200)
    EPS.event("e2").set("label", "3").sendAfter(300)
    EPS.event("e2").set("label", "4").sendAfter(400)
    EPS.event("e3").set("label", "5").sendAfter(500)
    EPS.event("e3").set("label", "6").sendAfter(600)
    EPS.event("e3").set("label", "7").sendAfter(700)
