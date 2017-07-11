# Sponge Knowledge base
# Script - Overriding
# Note that auto-enable is turned off in the configuration.

java_import  java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $EPS.setVariable("receivedEventA1", AtomicInteger.new(0))
    $EPS.setVariable("receivedEventA2", AtomicInteger.new(0))
    $EPS.setVariable("functionA1", AtomicInteger.new(0))
    $EPS.setVariable("functionA2", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def configure
        self.event = "a"
    end

    def run(event)
        $EPS.getVariable("receivedEventA1").set(1)
    end
end

# Execute immediately while loading
$EPS.enable(TriggerA)

class TriggerA < Trigger
    def configure
        self.event = "a"
    end

    def run(event)
        $EPS.getVariable("receivedEventA2").set(2)
    end
end

# Execute immediately while loading
$EPS.enable(TriggerA)

def onStartup
    $EPS.event("a").send()
    functionA()
end

def functionA()
    $EPS.getVariable("functionA1").set(1)
end

def functionA()
    $EPS.getVariable("functionA2").set(2)
end

