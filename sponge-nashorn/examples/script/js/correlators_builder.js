/**
 * Sponge Knowledge Base
 * Correlator builders
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger
var Collections = java.util.Collections
var LinkedHashMap = java.util.LinkedHashMap

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0))
    sponge.setVariable("eventLogs", Collections.synchronizedMap(new LinkedHashMap()))
}

function onLoad() {
    sponge.enable(new CorrelatorBuilder("SampleCorrelator").withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
            .withOnAcceptAsFirst(function(correlator, event) { return event.name == "filesystemFailure"})
            .withOnInit(function(correlator) { sponge.getVariable("eventLogs").put(correlator.meta.name, []) })
            .withOnEvent(function(correlator, event) {
                eventLog = sponge.getVariable("eventLogs").get(correlator.meta.name)

                eventLog.push(event);
                correlator.logger.debug("{} - event: {}, log: {}", correlator.hashCode(), event.name, eventLog.toString());
                sponge.getVariable("hardwareFailureScriptCount").incrementAndGet();
                if (eventLog.length >= 4) {
                    sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet();
                    correlator.finish();
                }
            }))
}

function onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server2").send();
    sponge.event("diskFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server2").send();
}