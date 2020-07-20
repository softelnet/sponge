# Sponge Knowledge Base
# Using knowledge base manager: enabling/disabling processors.
# Note that auto-enable is turned off in the configuration.

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $sponge.setVariable("verifyTriggerEnabled", AtomicBoolean.new(false))
    $sponge.setVariable("verifyTriggerDisabled", AtomicBoolean.new(false))
    $sponge.setVariable("verificationDone", AtomicBoolean.new(false))
end

def verifyManager
    triggerCount = $sponge.engine.triggers.size()
    $sponge.enable(TriggerA)
    $sponge.enable(TriggerA)
    $sponge.getVariable("verifyTriggerEnabled").set($sponge.engine.triggers.size() == triggerCount + 1)
    triggerCount = $sponge.engine.triggers.size()
    $sponge.disable(TriggerA)
    $sponge.disable(TriggerA)
    $sponge.getVariable("verifyTriggerDisabled").set($sponge.engine.triggers.size() == triggerCount - 1)
    $sponge.getVariable("verificationDone").set(true)
end

class VerifyTrigger < Trigger
    def onConfigure
        self.withEvent("verify")
    end
    def onRun(event)
        verifyManager()
    end
end

class TriggerA < Trigger
    def onConfigure
        self.withEvent("a")
    end
    def onRun(event)
    end
end

def onLoad
    $sponge.enable(VerifyTrigger)
end

def onStartup
    $sponge.event("verify").send()
end
