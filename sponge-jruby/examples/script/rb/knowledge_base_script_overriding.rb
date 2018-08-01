# Sponge Knowledge base
# Script - Overriding
# Note that auto-enable is turned off in the configuration.

java_import  java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $sponge.setVariable("receivedEventA1", AtomicInteger.new(0))
    $sponge.setVariable("receivedEventA2", AtomicInteger.new(0))
    $sponge.setVariable("functionA1", AtomicInteger.new(0))
    $sponge.setVariable("functionA2", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def onConfigure
        self.event = "a"
    end

    def onRun(event)
        $sponge.getVariable("receivedEventA1").set(1)
    end
end

# Execute immediately while loading
$sponge.enable(TriggerA)

class TriggerA < Trigger
    def onConfigure
        self.event = "a"
    end

    def onRun(event)
        $sponge.getVariable("receivedEventA2").set(2)
    end
end

# Execute immediately while loading
$sponge.enable(TriggerA)

def onStartup
    $sponge.event("a").send()
    functionA()
end

def functionA()
    $sponge.getVariable("functionA1").set(1)
end

def functionA()
    $sponge.getVariable("functionA2").set(2)
end

