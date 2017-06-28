# Sponge Knowledge base
# Using rules - events

java_import org.openksavi.sponge.core.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $EPS.setVariable("correlationEventsLog", $correlationEventsLog)
end

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF < Rule
    def configure
        self.events = ["e1", "e5 :none", "e6 :none", "e3 :first"]
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNF", self)
    end
end

class RuleFNNNL < Rule
    def configure
        self.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNNL", self)
    end
end

class RuleFNNNLReject < Rule
    def configure
        self.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNNLRejected", self)
    end
end

class RuleFNFNL < Rule
    def configure
        self.events = ["e1", "e5 :none", "e2 :first", "e7 :none", "e3 :last"]
        self.duration = Duration.ofSeconds(2)
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNFNL", self)
    end
end

def onStartup
    $EPS.event("e1").set("label", "1").sendAfter(100)
    $EPS.event("e2").set("label", "2").sendAfter(200)
    $EPS.event("e2").set("label", "3").sendAfter(300)
    $EPS.event("e2").set("label", "4").sendAfter(400)
    $EPS.event("e3").set("label", "5").sendAfter(500)
    $EPS.event("e3").set("label", "6").sendAfter(600)
    $EPS.event("e3").set("label", "7").sendAfter(700)
end
