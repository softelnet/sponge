"""
Sponge Knowledge base
Using rules - immediate, duration
"""

from org.openksavi.sponge.core.util import CorrelationEventsLog
from org.openksavi.sponge.core.event import EventId

def onInit():
    global defaultDuration, correlationEventsLog
    defaultDuration = 1

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

def runRule(rule):
    rule.logger.debug("Sequence: {}", Utils.getAbbreviatedEventSequenceString(rule))

    global correlationEventsLog
    correlationEventsLog.addEvents(rule.name, rule)

# Naming F(irst), L(ast), A(ll), N(one)

# F(irst)F(irst)F(irst)
class RuleFFF(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)F(irst)L(ast)
class RuleFFL(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)F(irst)A(ll)
class RuleFFA(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)F(irst)N(one)
class RuleFFN(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e4 :none"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)L(ast)F(irst)
class RuleFLF(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)L(ast)L(ast)
class RuleFLL(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)L(ast)A(ll)
class RuleFLA(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)L(ast)N(one)
class RuleFLN(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e4 :none"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)A(ll)F(irst)
class RuleFAF(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)A(ll)L(ast)
class RuleFAL(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)A(ll)A(ll)
class RuleFAA(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)A(ll)N(one)
class RuleFAN(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e5 :none"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)N(one)F(irst)
class RuleFNF(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)N(one)L(ast)
class RuleFNL(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

# F(irst)N(one)A(ll)
class RuleFNA(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

class RuleFNFReject(Rule):
    def configure(self):
        self.events = ["e1", "e2 :none", "e3"]
        global defaultDuration
        self.duration = Duration.ofSeconds(defaultDuration)
    def run(self, event):
        runRule(self)

def onStartup():
    EPS.event("e1").set("label", "1").sendAfter(100)
    EPS.event("e2").set("label", "2").sendAfter(200)
    EPS.event("e2").set("label", "3").sendAfter(300)
    EPS.event("e3").set("label", "4").sendAfter(400)
    EPS.event("e2").set("label", "5").sendAfter(500)
    EPS.event("e3").set("label", "6").sendAfter(600)
    EPS.event("e3").set("label", "7").sendAfter(700)
