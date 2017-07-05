# Sponge Knowledge base
# Using correlators

java_import org.openksavi.sponge.examples.SampleJavaCorrelator
java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $EPS.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $EPS.setVariable("hardwareFailureJavaCount", AtomicInteger.new(0))
end

class SampleCorrelator < Correlator
    @@instanceStarted = AtomicBoolean.new(false)
    def configure
        self.eventNames = ["filesystemFailure", "diskFailure"]
    end
    def init
        @eventLog = []
    end
    def acceptsAsFirst(event)
        return @@instanceStarted.compareAndSet(false, true)
    end
    def onEvent(event)
        @eventLog  << event
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, @eventLog.map(&:to_s))
        $EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if @eventLog.length >= 4
            self.finish()
        end
    end
end

def onLoad
    $EPS.enableJava(SampleJavaCorrelator)
end

def onStartup
    $EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    $EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    $EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
end