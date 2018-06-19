# Sponge Knowledge base
# Auto-enable

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $EPS.setVariable("counter", AtomicInteger.new(0))
end

class AutoAction < Action
    def onCall()
        self.logger.debug("Running")
        $EPS.getVariable("counter").incrementAndGet()
        return nil
    end
end

class AutoFilter < Filter
    def onConfigure
        self.event = "e1"
    end
    def onAccept(event)
        self.logger.debug("Received event: {}", event.name)
        $EPS.getVariable("counter").incrementAndGet()
        return true
    end
end

class AutoTrigger < Trigger
    def onConfigure
        self.event = "e1"
    end
    def onRun(event)
        self.logger.debug("Received event: {}", event.name)
        $EPS.getVariable("counter").incrementAndGet()
    end
end

class AutoRule < Rule
    def onConfigure
        self.events = ["e1", "e2"]
    end
    def onRun(event)
        self.logger.debug("Running for sequence: {}", self.eventSequence)
        $EPS.getVariable("counter").incrementAndGet()
    end
end

class AutoCorrelator < Correlator
    def onConfigure
        self.events = ["e1", "e2"]
    end
    def onAcceptAsFirst(event)
        return event.name == "e1"
    end
    def onEvent(event)
        self.logger.debug("Received event: {}", event.name)
        if event.name == "e2"
            $EPS.getVariable("counter").incrementAndGet()
            self.finish()
        end
    end
end

def onStartup
    $EPS.call("AutoAction")
    $EPS.event("e1").send()
    $EPS.event("e2").send()
end
