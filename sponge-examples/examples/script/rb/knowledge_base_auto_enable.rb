# Sponge Knowledge base
# Auto-enable

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $EPS.setVariable("counter", AtomicInteger.new(0))
end

class AutoAction < Action
    def run(args)
        self.logger.debug("Running")
        $EPS.getVariable("counter").incrementAndGet()
        return args
    end
end

class AutoFilter < Filter
    def configure
        self.eventName = "e1"
    end
    def accepts(event)
        self.logger.debug("Received event: {}", event.name)
        $EPS.getVariable("counter").incrementAndGet()
        return true
    end
end

class AutoTrigger < Trigger
    def configure
        self.eventName = "e1"
    end
    def run(event)
        self.logger.debug("Received event: {}", event.name)
        $EPS.getVariable("counter").incrementAndGet()
    end
end

class AutoRule < Rule
    def configure
        self.events = ["e1", "e2"]
    end
    def run(event)
        self.logger.debug("Running for sequence: {}", self.eventSequence)
        $EPS.getVariable("counter").incrementAndGet()
    end
end

class AutoAggregator < Aggregator
    def configure
        self.eventNames = ["e1", "e2"]
    end
    def acceptsAsFirst(event)
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
    $EPS.callAction("AutoAction")
    $EPS.event("e1").send()
    $EPS.event("e2").send()
end
