"""
Sponge Knowledge base
Using runordered ules - events
"""

from org.openksavi.sponge.test.util import CorrelationEventsLog

def onInit():
    global defaultDuration, correlationEventsLog
    defaultDuration = Duration.ofMillis(1000)

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    EPS.setVariable("correlationEventsLog", correlationEventsLog)

def run(rule):
    rule.logger.debug("Running rule {} for sequence: {}; alias map: {}", rule.hashCode(), Utils.toStringArrayEventSequence(rule.eventSequence, "label"),
                      rule.eventAliasMap)
    global correlationEventsLog
    correlationEventsLog.addEvents(rule.name, rule)

# Naming F(irst), L(ast), A(ll), N(one)
class RuleF(Rule):
    def onConfigure(self):
        self.events = ["a"]; self.ordered = False
    def onRun(self, event):
        run(self)

# F(irst)F(irst)F(irst)
class RuleFFF(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "a :first"]; self.ordered = False
    def onRun(self, event):
        run(self)

class RuleFFFDuration(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)F(irst)L(ast) Last is not significant in this case
class RuleFFL(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)F(irst)A(ll)
class RuleFFA(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)L(ast)F(irst)
class RuleFLF(Rule):
    def onConfigure(self):
        self.events = ["c", "b :last", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)L(ast)L(ast)
class RuleFLL(Rule):
    def onConfigure(self):
        self.events = ["c", "b :last", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)L(ast)A(ll)
class RuleFLA(Rule):
    def onConfigure(self):
        self.events = ["c", "b :last", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)A(ll)F(irst)
class RuleFAF(Rule):
    def onConfigure(self):
        self.events = ["c", "b :all", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)A(ll)L(ast)
class RuleFAL(Rule):
    def onConfigure(self):
        self.events = ["c", "b :all", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)A(ll)A(ll)
class RuleFAA(Rule):
    def onConfigure(self):
        self.events = ["c", "b :all", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)F(irst)N(one)
class RuleFFN(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "d :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)L(ast)N(one)
class RuleFLN(Rule):
    def onConfigure(self):
        self.events = ["c", "b :last", "d :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)A(ll)N(one)
class RuleFAN(Rule):
    def onConfigure(self):
        self.events = ["c", "b :all", "e :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)N(one)F(irst)
class RuleFNF(Rule):
    def onConfigure(self):
        self.events = ["c", "e :none", "a"]; self.ordered = False
    def onRun(self, event):
        run(self)

# F(irst)N(one)L(ast)
class RuleFNL(Rule):
    def onConfigure(self):
        self.events = ["c", "e :none", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# F(irst)N(one)A(ll)
class RuleFNA(Rule):
    def onConfigure(self):
        self.events = ["c", "e :none", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

# Reject

class RuleFFNReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFLNReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :last", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFANReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :all", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFNNReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :none", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFNFReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :none", "a"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFNLReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :none", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleFNAReject(Rule):
    def onConfigure(self):
        self.events = ["c", "b :none", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleLFNReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :first", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLLNReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :last", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLANReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :all", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLNNReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :none", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLNFReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :none", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLNLReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :none", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleLNAReject(Rule):
    def onConfigure(self):
        self.events = ["c :last", "b :none", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleAFNReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :first", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleALNReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :last", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleAANReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :all", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleANNReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :none", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleANFReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :none", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleANLReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :none", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleANAReject(Rule):
    def onConfigure(self):
        self.events = ["c :all", "b :none", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

class RuleNFNReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :first", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNLNReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :last", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNANReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :all", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNNNReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :none", "a :none"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNNFReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :none", "a :first"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNNLReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :none", "a :last"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)
class RuleNNAReject(Rule):
    def onConfigure(self):
        self.events = ["c :none", "b :none", "a :all"]; self.ordered = False
        global defaultDuration; self.duration = defaultDuration
    def onRun(self, event):
        run(self)

def onStartup():
    EPS.event("a").set("label", "a1").send()
    EPS.event("b").set("label", "b1").send()
    EPS.event("b").set("label", "b2").send()
    EPS.event("b").set("label", "b3").send()
    EPS.event("c").set("label", "c1").send()
    EPS.event("c").set("label", "c2").send()
    EPS.event("a").set("label", "a2").send()
    EPS.event("c").set("label", "c3").send()

