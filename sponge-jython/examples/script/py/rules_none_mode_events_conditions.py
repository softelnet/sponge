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

class RuleFNF(Rule):
    def configure(self):
        self.events = ["e1", "e2 :none", "e3"]
        self.setConditions("e2", self.e2LabelCondition)
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNF", self)
    def e2LabelCondition(self, event):
        return int(event.get("label")) > 4

class RuleFNNFReject(Rule):
    def configure(self):
        self.events = ["e1", "e2 :none", "e6 :none", "e3"]
        self.setConditions("e2", self.e2LabelCondition)
    def run(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNFReject", self)
    def e2LabelCondition(self, event):
        return 2 <= int(event.get("label")) <= 4

def onStartup():
    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
