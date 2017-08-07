# Sponge Knowledge base
# Using rules - synchronous and asynchronous

java_import org.openksavi.sponge.test.util.CorrelationEventsLog

def onInit
    # Variables for assertions only
    $correlationEventsLog = CorrelationEventsLog.new
    $EPS.setVariable("correlationEventsLog", $correlationEventsLog)
end

class RuleFFF < Rule
    def onConfigure
        self.events = ["e1", "e2", "e3 :first"]
        self.synchronous = true
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFF", self)
    end
end

class RuleFFL < Rule
    def onConfigure
        self.events = ["e1", "e2", "e3 :last"]
        self.duration = Duration.ofMillis(500)
        self.synchronous = false
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $correlationEventsLog.addEvents("RuleFFL", self)
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
