# Sponge Knowledge base
# Using rules - synchronous and asynchronous

java_import org.openksavi.sponge.test.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $EPS.setVariable("correlationEventsLog", $correlationEventsLog)
end

class RuleFFF < Rule
    def configure
        self.events = ["e1", "e2", "e3 :first"]
        self.synchronous = true
    end
    def run(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFF", self)
    end
end

class RuleFFL < Rule
    def configure
        self.events = ["e1", "e2", "e3 :last"]
        self.duration = Duration.ofMillis(500)
        self.synchronous = false
    end
    def run(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFL", self)
    end
end

def onStartup
    $EPS.event("e1").set("label", "1").sendAfter(1)
    $EPS.event("e2").set("label", "2").sendAfter(2)
    $EPS.event("e2").set("label", "3").sendAfter(3)
    $EPS.event("e2").set("label", "4").sendAfter(4)
    $EPS.event("e3").set("label", "5").sendAfter(5)
    $EPS.event("e3").set("label", "6").sendAfter(6)
    $EPS.event("e3").set("label", "7").sendAfter(7)
end
