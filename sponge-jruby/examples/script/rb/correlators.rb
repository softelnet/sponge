# Sponge Knowledge base
# Using correlators

java_import org.openksavi.sponge.examples.SampleJavaCorrelator
java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $sponge.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $sponge.setVariable("hardwareFailureJavaCount", AtomicInteger.new(0))
    $sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger.new(0))
    $sponge.setVariable("hardwareFailureJavaFinishCount", AtomicInteger.new(0))
end

class SampleCorrelator < Correlator
    def onConfigure
        self.withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
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
        $sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if @eventLog.length >= 4
            $sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            self.finish()
        end
    end
end

def onLoad
    $sponge.enableJava(SampleJavaCorrelator)
end

def onStartup
    $sponge.event("filesystemFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server2").send()
    $sponge.event("diskFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server2").send()
end
