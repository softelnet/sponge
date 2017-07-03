"""
Sponge Knowledge base
Using rules - events
"""

from org.openksavi.sponge.test.util import CorrelationEventsLog

def onInit():
    global defaultDuration, correlationEventsLog
    defaultDuration = 10

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

# Naming F(irst), L(ast), A(ll), N(one)

class RuleF(Rule):
    def configure(self):
        self.events = ["e1"]
    def run(self, event):
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleF", self)

# F(irst)F(irst)F(irst)
class RuleFFF(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :first"]
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFF", self)

class RuleFFFDuration(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}", event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFFDuration", self)

# F(irst)F(irst)L(ast)
class RuleFFL(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFL", self)

# F(irst)F(irst)A(ll)
class RuleFFA(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFA", self)

# F(irst)F(irst)N(one)
class RuleFFN(Rule):
    def configure(self):
        self.events = ["e1", "e2", "e4 :none"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFFN", self)

# F(irst)L(ast)F(irst)
class RuleFLF(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFLF", self)

# F(irst)L(ast)L(ast)
class RuleFLL(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFLL", self)

# F(irst)L(ast)A(ll)
class RuleFLA(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFLA", self)

# F(irst)L(ast)N(one)
class RuleFLN(Rule):
    def configure(self):
        self.events = ["e1", "e2 :last", "e4 :none"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFLN", self)

# F(irst)A(ll)F(irst)
class RuleFAF(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :first"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFAF", self)

# F(irst)A(ll)L(ast)
class RuleFAL(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFAL", self)

# F(irst)A(ll)A(ll)
class RuleFAA(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFAA", self)

# F(irst)A(ll)N(one)
class RuleFAN(Rule):
    def configure(self):
        self.events = ["e1", "e2 :all", "e5 :none"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFAN", self)

# F(irst)N(one)F(irst)
class RuleFNF(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3"]
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNF", self)

# F(irst)N(one)L(ast)
class RuleFNL(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3 :last"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNL", self)

# F(irst)N(one)A(ll)
class RuleFNA(Rule):
    def configure(self):
        self.events = ["e1", "e5 :none", "e3 :all"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNA", self)

class RuleFNFReject(Rule):
    def configure(self):
        self.events = ["e1", "e2 :none", "e3"]
        global defaultDuration
        self.duration = Duration.ofMillis(defaultDuration)
    def run(self, event):
        self.logger.debug("Running rule for sequence: {}", Utils.toStringEventSequence(self.eventSequence, "label"))
        global correlationEventsLog
        correlationEventsLog.addEvents("RuleFNFReject", self)

def onStartup():
    EPS.event("e1").set("label", "0").sendAfter(0, 10)  # Not used in assertions, "background noise" events.
    EPS.event("e1").set("label", "-1").sendAfter(0, 10)
    EPS.event("e1").set("label", "-2").sendAfter(0, 10)
    EPS.event("e1").set("label", "-3").sendAfter(0, 10)

    EPS.event("e1").set("label", "1").sendAfter(1)
    EPS.event("e2").set("label", "2").sendAfter(2)
    EPS.event("e2").set("label", "3").sendAfter(3)
    EPS.event("e2").set("label", "4").sendAfter(4)
    EPS.event("e3").set("label", "5").sendAfter(5)
    EPS.event("e3").set("label", "6").sendAfter(6)
    EPS.event("e3").set("label", "7").sendAfter(7)
