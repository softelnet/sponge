"""
Sponge Knowledge Base
Rules load test.
"""

from org.openksavi.sponge.examples.util import CorrelationEventsLog
import time

def onInit():
    global defaultDuration, correlationEventsLog
    defaultDuration = 1000

    # Variables for assertions only
    correlationEventsLog = CorrelationEventsLog()
    sponge.setVariable("correlationEventsLog", correlationEventsLog)

def updateLog(rule):
    for event in rule.eventSequence:
        if event is not None and int(event.get("label")) < 1:
            return
    #rule.logger.debug("Running rule for sequence: {}", SpongeUtils.toStringEventSequence(rule.eventSequence, "label"))
    global correlationEventsLog
    correlationEventsLog.addEvents(rule.meta.name, rule)

# Naming F(irst), L(ast), A(ll), N(one)

class RuleF(Rule):
    def onConfigure(self):
        self.withEvents(["e1"])
    def onRun(self, event):
        updateLog(self)

# F(irst)F(irst)F(irst)
class RuleFFF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e3 :first"])
    def onRun(self, event):
        updateLog(self)

class RuleFFFDuration(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e3 :first"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)F(irst)L(ast)
class RuleFFL(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e3 :last"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)F(irst)A(ll)
class RuleFFA(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e3 :all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)F(irst)N(one)
class RuleFFN(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2", "e4 :none"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)L(ast)F(irst)
class RuleFLF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :last", "e3 :first"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)L(ast)L(ast)
class RuleFLL(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :last", "e3 :last"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)L(ast)A(ll)
class RuleFLA(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :last", "e3 :all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)L(ast)N(one)
class RuleFLN(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :last", "e4 :none"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)A(ll)F(irst)
class RuleFAF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :all", "e3 :first"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)A(ll)L(ast)
class RuleFAL(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :all", "e3 :last"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)A(ll)A(ll)
class RuleFAA(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :all", "e3 :all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)A(ll)N(one)
class RuleFAN(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :all", "e5 :none"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)N(one)F(irst)
class RuleFNF(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e5 :none", "e3"])
    def onRun(self, event):
        updateLog(self)

# F(irst)N(one)L(ast)
class RuleFNL(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e5 :none", "e3 :last"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

# F(irst)N(one)A(ll)
class RuleFNA(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e5 :none", "e3 :all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)

class RuleFNFReject(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2 :none", "e3"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        updateLog(self)


class Stats(Trigger):
    def onConfigure(self):
        self.withEvent("stats")
    def onRun(self, event):
        self.logger.debug("Statistics: {}", sponge.engine.statisticsManager.summary)

class RuleA(Rule):
    def onConfigure(self):
        self.withEvents(["a1", "a2:all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        pass

class RuleB(Rule):
    def onConfigure(self):
        self.withEvents(["b1", "b2:all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        pass

class RuleC(Rule):
    def onConfigure(self):
        self.withEvents(["c1", "c2:all"])
        global defaultDuration
        self.withDuration(Duration.ofMillis(defaultDuration))
    def onRun(self, event):
        pass

class SendEvents(Trigger):
    def onConfigure(self):
        self.withEvent("sendEvents")
    def onRun(self, event):
        for event in ["a1", "a2", "b1", "b2", "c1", "c2"]:
            sponge.event(event).send()
        sponge.event("e1").set("label", "1").send()
        sponge.event("e2").set("label", "2").send()
        sponge.event("e2").set("label", "3").send()
        sponge.event("e2").set("label", "4").send()
        sponge.event("e3").set("label", "5").send()
        sponge.event("e3").set("label", "6").send()
        sponge.event("e3").set("label", "7").send()

class SendNoise(Trigger):
    def onConfigure(self):
        self.withEvent("sendNoise")
    def onRun(self, event):
        while sponge.engine.isRunning():
            if sponge.engine.getEventQueueManager().getInputEventQueue().getSize() < 100:
                # Not used in assertions, "background noise" events.
                sponge.event("e1").set("label", "0").send()
                sponge.event("e1").set("label", "-1").send()
                sponge.event("e1").set("label", "-2").send()
                sponge.event("e1").set("label", "-3").send()
            time.sleep(.10)

def onStartup():
    sponge.event("stats").sendAfter(0, 10000)
    sponge.event("sendNoise").send()
