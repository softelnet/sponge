# Sponge Knowledge base
# Using correlator duration

java_import org.openksavi.sponge.examples.SampleJavaCorrelator
java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $EPS.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
end

class SampleCorrelator < Correlator
    @@instanceStarted = AtomicBoolean.new(false)
    def configure
        self.eventNames = ["filesystemFailure", "diskFailure"]
        self.duration = Duration.ofSeconds(2)
    end
    def init
        @eventLog = []
    end
    def acceptsAsFirst(event)
        return @@instanceStarted.compareAndSet(false, true)
    end
    def onEvent(event)
        @eventLog  << event
        $EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    end
    def onDuration()
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, @eventLog.map(&:to_s))
    end
end

def onStartup
    $EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    $EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    $EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
end