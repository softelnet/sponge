# Sponge Knowledge base
# Triggers - Event pattern

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $EPS.setVariable("countA", AtomicInteger.new(0))
    $EPS.setVariable("countAPattern", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def onConfigure
        self.event = "a"
    end
    def onRun(event)
        $EPS.getVariable("countA").incrementAndGet()
    end
end

class TriggerAPattern < Trigger
    def onConfigure
        self.event = "a.*"
    end
    def onRun(event)
        self.logger.debug("Received matching event {}", event.name)
        $EPS.getVariable("countAPattern").incrementAndGet()
    end
end

def onStartup
    for name in ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ]
        $EPS.event(name).send()
    end
end
