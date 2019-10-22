"""
Sponge Knowledge base
Correlator builders
"""

from java.util.concurrent.atomic import AtomicInteger
from java.util import Collections, LinkedHashMap

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
    sponge.setVariable("eventLogs", Collections.synchronizedMap(LinkedHashMap()))

def onLoad():
    def onEvent(correlator, event):
        eventLog = sponge.getVariable("eventLogs").get(correlator.meta.name)

        eventLog.append(event)
        correlator.logger.debug("{} - event: {}, log: {}", correlator.hashCode(), event.name, str(eventLog))
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if len(eventLog) == 4:
            sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            correlator.finish()

    sponge.enable(CorrelatorBuilder("SampleCorrelator").withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
                .withOnAcceptAsFirst(lambda  correlator, event: event.name == "filesystemFailure")
                .withOnInit(lambda  correlator: sponge.getVariable("eventLogs").put(correlator.meta.name, []))
                .withOnEvent(onEvent))

def onStartup():
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
