# Sponge Knowledge base
# Triggers - Generating events and using triggers

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean
java_import org.openksavi.sponge.examples.SampleJavaTrigger

def onInit
    # Variables for assertions only
    $EPS.setVariable("receivedEventA", AtomicBoolean.new(false))
    $EPS.setVariable("receivedEventBCount", AtomicInteger.new(0))
    $EPS.setVariable("receivedEventTestJavaCount", AtomicInteger.new(0))
end

class TriggerA < Trigger
    def configure
        self.event = "a"
    end
    def run(event)
        self.logger.debug("Received event: {}", event.name)
        $EPS.getVariable("receivedEventA").set(true)
    end
end

class TriggerB < Trigger
    def configure
        self.event = "b"
    end
    def run(event)
        self.logger.debug("Received event: {}", event.name)
        if $EPS.getVariable("receivedEventBCount").get() == 0
            self.logger.debug("Statistics: {}", $EPS.statisticsSummary)
        end
        $EPS.getVariable("receivedEventBCount").incrementAndGet
    end
end

def onLoad
    $EPS.enableJava(SampleJavaTrigger)
end

def onStartup
    $EPS.logger.debug("Startup {}, triggers: {}", $EPS.description, $EPS.engine.triggers)
    $EPS.logger.debug("Knowledge base name: {}", $EPS.kb.name)
    $EPS.event("a").send()
    $EPS.event("b").sendAfter(200, 200)
    $EPS.event("testJava").send()
end

def onShutdown
    $EPS.logger.debug("Shutting down")
end
