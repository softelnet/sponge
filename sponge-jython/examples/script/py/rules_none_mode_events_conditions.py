"""
Sponge Knowledge base
Using rules - events
"""

from org.openksavi.sponge.examples.util import CorrelationEventsLog

def onInit():
    global correlationEventsLog

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    sponge.setVariable("correlationEventsLog", correlationEventsLog)

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :none", "e3"]).withCondition("e2", self.e2LabelCondition)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNF", self)
    def e2LabelCondition(self, event):
        return int(event.get("label")) > 4

class RuleFNNFReject(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :none", "e6 :none", "e3"]).withCondition("e2", self.e2LabelCondition)
    def onRun(self, event):
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNNFReject", self)
    def e2LabelCondition(self, event):
        return 2 <= int(event.get("label")) <= 4

def onStartup():
    sponge.event("e1").set("label", "1").send()
    sponge.event("e2").set("label", "2").send()
    sponge.event("e2").set("label", "3").send()
    sponge.event("e2").set("label", "4").send()
    sponge.event("e3").set("label", "5").send()
    sponge.event("e3").set("label", "6").send()
    sponge.event("e3").set("label", "7").send()
