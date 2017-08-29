# Sponge Knowledge base
# Using correlators

java_import org.openksavi.sponge.examples.SampleJavaCorrelator
java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $EPS.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $EPS.setVariable("hardwareFailureJavaCount", AtomicInteger.new(0))
    $EPS.setVariable("hardwareFailureScriptFinishCount", AtomicInteger.new(0))
    $EPS.setVariable("hardwareFailureJavaFinishCount", AtomicInteger.new(0))
end

class SampleCorrelator < Correlator
    def onConfigure
        self.events = ["filesystemFailure", "diskFailure"]
        self.maxInstances = 1
    end
    def onAcceptAsFirst(event)
        return event.name == "filesystemFailure"
    end
    def onInit
        @eventLog = []
    end
    def onEvent(event)
        @eventLog  << event
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, @eventLog.map(&:to_s))
        $EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if @eventLog.length >= 4
            $EPS.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            self.finish()
        end
    end
end

def onLoad
    $EPS.enableJava(SampleJavaCorrelator)
end

def onStartup
    $EPS.event("filesystemFailure").set("source", "server1").send()
    $EPS.event("diskFailure").set("source", "server1").send()
    $EPS.event("diskFailure").set("source", "server2").send()
    $EPS.event("diskFailure").set("source", "server1").send()
    $EPS.event("diskFailure").set("source", "server2").send()
end
