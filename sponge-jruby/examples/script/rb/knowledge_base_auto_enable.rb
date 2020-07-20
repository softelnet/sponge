# Sponge Knowledge Base
# Auto-enable

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $sponge.setVariable("counter", AtomicInteger.new(0))
end

class AutoAction < Action
    def onCall()
        self.logger.debug("Running")
        $sponge.getVariable("counter").incrementAndGet()
        return nil
    end
end

class AutoFilter < Filter
    def onConfigure
        self.withEvent("e1")
    end
    def onAccept(event)
        self.logger.debug("Received event: {}", event.name)
        $sponge.getVariable("counter").incrementAndGet()
        return true
    end
end

class AutoTrigger < Trigger
    def onConfigure
        self.withEvent("e1")
    end
    def onRun(event)
        self.logger.debug("Received event: {}", event.name)
        $sponge.getVariable("counter").incrementAndGet()
    end
end

class AutoRule < Rule
    def onConfigure
        self.withEvents(["e1", "e2"])
    end
    def onRun(event)
        self.logger.debug("Running for sequence: {}", self.eventSequence)
        $sponge.getVariable("counter").incrementAndGet()
    end
end

class AutoCorrelator < Correlator
    def onConfigure
        self.withEvents(["e1", "e2"])
    end
    def onAcceptAsFirst(event)
        return event.name == "e1"
    end
    def onEvent(event)
        self.logger.debug("Received event: {}", event.name)
        if event.name == "e2"
            $sponge.getVariable("counter").incrementAndGet()
            self.finish()
        end
    end
end

def onStartup
    $sponge.call("AutoAction")
    $sponge.event("e1").send()
    $sponge.event("e2").send()
end
