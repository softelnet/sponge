/**
 * Sponge Knowledge base
 * Correlator builders
 */

import java.util.concurrent.atomic.*
import java.util.*

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0))
    sponge.setVariable("eventLogs", Collections.synchronizedMap(new LinkedHashMap()))
}

void onLoad() {
    sponge.enable(new CorrelatorBuilder("SampleCorrelator").withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
                .withOnAcceptAsFirst({ correlator, event -> event.name == "filesystemFailure"})
                .withOnInit({ correlator -> sponge.getVariable("eventLogs").put(correlator.meta.name, []) })
                .withOnEvent({ correlator, event ->
                    eventLog = sponge.getVariable("eventLogs").get(correlator.meta.name)

                    eventLog << event
                    correlator.logger.debug("{} - event: {}, log: {}", correlator.hashCode(), event.name, eventLog)
                    sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
                    if (eventLog.size() >= 4) {
                        sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
                        correlator.finish()
                    }
                }))
}

void onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
}
