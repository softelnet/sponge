# Sponge Knowledge base
# Using rules - events

java_import org.openksavi.sponge.test.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $sponge.setVariable("correlationEventsLog", $correlationEventsLog)
end

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF < Rule
    def onConfigure
        self.events = ["e1", "e5 :none", "e6 :none", "e3 :first"]
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNF", self)
    end
end

class RuleFNNNL < Rule
    def onConfigure
        self.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNNL", self)
    end
end

class RuleFNNNLReject < Rule
    def onConfigure
        self.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNNLRejected", self)
    end
end

class RuleFNFNL < Rule
    def onConfigure
        self.events = ["e1", "e5 :none", "e2 :first", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNFNL", self)
    end
end

def onStartup
    $sponge.event("e1").set("label", "1").send()
    $sponge.event("e2").set("label", "2").send()
    $sponge.event("e2").set("label", "3").send()
    $sponge.event("e2").set("label", "4").send()
    $sponge.event("e3").set("label", "5").send()
    $sponge.event("e3").set("label", "6").send()
    $sponge.event("e3").set("label", "7").send()
end
