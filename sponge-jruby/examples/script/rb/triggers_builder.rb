# Sponge Knowledge Base
# Trigger builders

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $sponge.setVariable("receivedEventA", AtomicBoolean.new(false))
    $sponge.setVariable("receivedEventBCount", AtomicInteger.new(0))
end

def onLoad
    $sponge.enable(TriggerBuilder.new("TriggerA").withEvent("a").withOnRun {|trigger, event|
        trigger.logger.debug("Received event: {}", event.name)
        $sponge.getVariable("receivedEventA").set(true)
    })

    $sponge.enable(TriggerBuilder.new("TriggerB").withEvent("b").withOnRun {|trigger, event|
        trigger.logger.debug("Received event: {}", event.name)
        if $sponge.getVariable("receivedEventBCount").get() == 0
            trigger.logger.debug("Statistics: {}", $sponge.statisticsSummary)
        end
        $sponge.getVariable("receivedEventBCount").incrementAndGet
    })
end

def onStartup
    $sponge.logger.debug("Startup {}, triggers: {}", $sponge.info, $sponge.engine.triggers)
    $sponge.logger.debug("Knowledge base name: {}", $sponge.kb.name)
    $sponge.event("a").send()
    $sponge.event("b").sendAfter(200, 200)
end
