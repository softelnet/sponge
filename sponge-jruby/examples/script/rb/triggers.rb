# Sponge Knowledge Base
# Triggers - Generating events and using triggers

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean
java_import org.openksavi.sponge.examples.SampleJavaTrigger

def onInit
    # Variables for assertions only
    $sponge.setVariable("receivedEventA", AtomicBoolean.new(false))
    $sponge.setVariable("receivedEventBCount", AtomicInteger.new(0))
    $sponge.setVariable("receivedEventTestJavaCount", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def onConfigure
        self.withEvent("a")
    end
    def onRun(event)
        self.logger.debug("Received event: {}", event.name)
        $sponge.getVariable("receivedEventA").set(true)
    end
end

class TriggerB < Trigger
    def onConfigure
        self.withEvent("b")
    end
    def onRun(event)
        self.logger.debug("Received event: {}", event.name)
        if $sponge.getVariable("receivedEventBCount").get() == 0
            self.logger.debug("Statistics: {}", $sponge.statisticsSummary)
        end
        $sponge.getVariable("receivedEventBCount").incrementAndGet
    end
end

def onLoad
    $sponge.enableJava(SampleJavaTrigger)
end

def onStartup
    $sponge.logger.debug("Startup {}, triggers: {}", $sponge.info, $sponge.engine.triggers)
    $sponge.logger.debug("Knowledge base name: {}", $sponge.kb.name)
    $sponge.event("a").send()
    $sponge.event("b").sendAfter(200, 200)
    $sponge.event("testJava").send()
end

def onShutdown
    $sponge.logger.debug("Shutting down")
end
