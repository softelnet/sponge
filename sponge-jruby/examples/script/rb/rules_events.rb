# Sponge Knowledge Base
# Using rules - events

java_import org.openksavi.sponge.examples.util.CorrelationEventsLog

def onInit
    $defaultDuration = 1000

    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $sponge.setVariable("correlationEventsLog", $correlationEventsLog)
end

# Naming F(irst), L(ast), A(ll), N(one)

class RuleF < Rule
    def onConfigure
        self.withEvent("e1")
    end
    def onRun(event)
        $correlationEventsLog.addEvents("RuleF", self)
    end
end

# F(irst)F(irst)F(irst)
class RuleFFF < Rule
    def onConfigure
        self.withEvents(["e1", "e2", "e3 :first"])
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFF", self)
    end
end

class RuleFFFDuration < Rule
    def onConfigure
        self.withEvents(["e1", "e2", "e3 :first"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFFDuration", self)
    end
end

# F(irst)F(irst)L(ast)
class RuleFFL < Rule
    def onConfigure
        self.withEvents(["e1", "e2", "e3 :last"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFL", self)
    end
end

# F(irst)F(irst)A(ll)
class RuleFFA < Rule
    def onConfigure
        self.withEvents(["e1", "e2", "e3 :all"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFFA", self)
    end
end

# F(irst)F(irst)N(one)
class RuleFFN < Rule
    def onConfigure
        self.withEvents(["e1", "e2", "e4 :none"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFFN", self)
    end
end

# F(irst)L(ast)F(irst)
class RuleFLF < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :last", "e3 :first"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFLF", self)
    end
end


# F(irst)L(ast)L(ast)
class RuleFLL < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :last", "e3 :last"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFLL", self)
    end
end

# F(irst)L(ast)A(ll)
class RuleFLA < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :last", "e3 :all"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFLA", self)
    end
end

# F(irst)L(ast)N(one)
class RuleFLN < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :last", "e4 :none"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFLN", self)
    end
end

# F(irst)A(ll)F(irst)
class RuleFAF < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :all", "e3 :first"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFAF", self)
    end
end

# F(irst)A(ll)L(ast)
class RuleFAL < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :all", "e3 :last"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFAL", self)
    end
end

# F(irst)A(ll)A(ll)
class RuleFAA < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :all", "e3 :all"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence)
        $correlationEventsLog.addEvents("RuleFAA", self)
    end
end

# F(irst)A(ll)N(one)
class RuleFAN < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :all", "e5 :none"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFAN", self)
    end
end

# F(irst)N(one)F(irst)
class RuleFNF < Rule
    def onConfigure
        self.withEvents(["e1", "e5 :none", "e3"])
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFNF", self)
    end
end

# F(irst)N(one)L(ast)
class RuleFNL < Rule
    def onConfigure
        self.withEvents(["e1", "e5 :none", "e3 :last"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFNL", self)
    end
end

# F(irst)N(one)A(ll)
class RuleFNA < Rule
    def onConfigure
        self.withEvents(["e1", "e5 :none", "e3 :all"]).withDuration( Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFNA", self)
    end
end

class RuleFNFReject < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :none", "e3"]).withDuration(Duration.ofMillis($defaultDuration))
    end
    def onRun(event)
        self.logger.debug("Running rule for sequence: {}", self.eventSequence)
        $correlationEventsLog.addEvents("RuleFNFReject", self)
    end
end

def onStartup
    $sponge.event("e1").set("label", "0").sendAfter(0, 200)  # Not used in assertions, "background noise" events.
    $sponge.event("e1").set("label", "-1").sendAfter(0, 200)
    $sponge.event("e1").set("label", "-2").sendAfter(0, 200)
    $sponge.event("e1").set("label", "-3").sendAfter(0, 200)

    $sponge.event("e1").set("label", "1").send()
    $sponge.event("e2").set("label", "2").send()
    $sponge.event("e2").set("label", "3").send()
    $sponge.event("e2").set("label", "4").send()
    $sponge.event("e3").set("label", "5").send()
    $sponge.event("e3").set("label", "6").send()
    $sponge.event("e3").set("label", "7").send()
end
