# Sponge Knowledge base
# Correlator builders

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.Collections
java_import java.util.LinkedHashMap

def onInit
    # Variables for assertions only
    $sponge.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger.new(0))
    $sponge.setVariable("eventLogs", Collections.synchronizedMap(LinkedHashMap.new()))
end

def onLoad
    $sponge.enable(CorrelatorBuilder.new("SampleCorrelator").withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
        .withOnAcceptAsFirst { |correlator, event| event.name == "filesystemFailure" }
        .withOnInit { |correlator| $sponge.getVariable("eventLogs").put(correlator.meta.name, []) }
        .withOnEvent { |correlator, event|
            eventLog = $sponge.getVariable("eventLogs").get(correlator.meta.name)

            eventLog  << event
            correlator.logger.debug("{} - event: {}, log: {}", correlator.hashCode(), event.name, eventLog.map(&:to_s))
            $sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
            if eventLog.length >= 4
                $sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
                correlator.finish()
            end
        })
end

def onStartup
    $sponge.event("filesystemFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server2").send()
    $sponge.event("diskFailure").set("source", "server1").send()
    $sponge.event("diskFailure").set("source", "server2").send()
end
