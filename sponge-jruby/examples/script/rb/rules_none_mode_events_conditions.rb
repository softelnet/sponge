# Sponge Knowledge base
# Using rules - events

java_import org.openksavi.sponge.test.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $EPS.setVariable("correlationEventsLog", $correlationEventsLog)
end

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF < Rule
    def configure
        self.events = ["e1", "e2 :none", "e3 :first"]
        self.setConditions("e2", self.method(:e2LabelCondition))
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNF", self)
    end
    def e2LabelCondition(event)
        return Integer(event.get("label")) > 4
    end
end

class RuleFNNFReject < Rule
    def configure
        self.events = ["e1", "e2 :none", "e6 :none", "e3 :first"]
        self.setConditions("e2", self.method(:e2LabelCondition))
    end
    def run(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNFReject", self)
    end
    def e2LabelCondition(event)
        label = Integer(event.get("label"))
        return 2 <= label && label <= 4
    end
end

def onStartup
    $EPS.event("e1").set("label", "1").send()
    $EPS.event("e2").set("label", "2").send()
    $EPS.event("e2").set("label", "3").send()
    $EPS.event("e2").set("label", "4").send()
    $EPS.event("e3").set("label", "5").send()
    $EPS.event("e3").set("label", "6").send()
    $EPS.event("e3").set("label", "7").send()
end
