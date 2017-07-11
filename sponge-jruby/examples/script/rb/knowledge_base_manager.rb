# Sponge Knowledge base
# Using knowledge base manager: enabling/disabling processors.
# Note that auto-enable is turned off in the configuration.

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $EPS.setVariable("verifyTriggerEnabled", AtomicBoolean.new(false))
    $EPS.setVariable("verifyTriggerDisabled", AtomicBoolean.new(false))
    $EPS.setVariable("verificationDone", AtomicBoolean.new(false))
end

def verifyManager
    triggerCount = $EPS.engine.triggers.size()
    $EPS.enable(TriggerA)
    $EPS.enable(TriggerA)
    $EPS.getVariable("verifyTriggerEnabled").set($EPS.engine.triggers.size() == triggerCount + 1)
    triggerCount = $EPS.engine.triggers.size()
    $EPS.disable(TriggerA)
    $EPS.disable(TriggerA)
    $EPS.getVariable("verifyTriggerDisabled").set($EPS.engine.triggers.size() == triggerCount - 1)
    $EPS.getVariable("verificationDone").set(true)
end

class VerifyTrigger < Trigger
    def configure
        self.event = "verify"
    end
    def run(event)
        verifyManager()
    end
end

class TriggerA < Trigger
    def configure
        self.event = "a"
    end
    def run(event)
    end
end

def onLoad
    $EPS.enable(VerifyTrigger)
end

def onStartup
    $EPS.event("verify").send()
end
