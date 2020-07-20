# Sponge Knowledge Base
# Triggers - Event pattern

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $sponge.setVariable("countA", AtomicInteger.new(0))
    $sponge.setVariable("countAPattern", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def onConfigure
        self.withEvent("a")
    end
    def onRun(event)
        $sponge.getVariable("countA").incrementAndGet()
    end
end

class TriggerAPattern < Trigger
    def onConfigure
        self.withEvent("a.*")
    end
    def onRun(event)
        self.logger.debug("Received matching event {}", event.name)
        $sponge.getVariable("countAPattern").incrementAndGet()
    end
end

def onStartup
    for name in ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ]
        $sponge.event(name).send()
    end
end
