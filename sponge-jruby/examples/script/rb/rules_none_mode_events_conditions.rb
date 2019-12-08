# Sponge Knowledge base
# Using rules - events

java_import org.openksavi.sponge.examples.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $sponge.setVariable("correlationEventsLog", $correlationEventsLog)
end

# Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :none", "e3 :first"]).withCondition("e2", self.method(:e2LabelCondition))
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNF", self)
    end
    def e2LabelCondition(event)
        return Integer(event.get("label")) > 4
    end
end

class RuleFNNFReject < Rule
    def onConfigure
        self.withEvents(["e1", "e2 :none", "e6 :none", "e3 :first"]).withCondition("e2", self.method(:e2LabelCondition))
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventAliasMap)
        $correlationEventsLog.addEvents("RuleFNNFReject", self)
    end
    def e2LabelCondition(event)
        label = Integer(event.get("label"))
        return 2 <= label && label <= 4
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
