/**
 * Sponge Knowledge base
 * Using correlators
 */

import org.openksavi.sponge.examples.SampleJavaCorrelator

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureJavaFinishCount", new AtomicInteger(0))
}

class SampleCorrelator extends Correlator {
    private eventLog

    void onConfigure() {
        this.withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1)
    }
    boolean onAcceptAsFirst(Event event) {
        return event.name == "filesystemFailure"
    }
    void onInit() {
        eventLog = []
    }
    void onEvent(Event event) {
        eventLog << event
        this.logger.debug("{} - event: {}, log: {}", this.hashCode(), event.name, eventLog)
        //hardwareFailureScriptCount.incrementAndGet()
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if (eventLog.size() >= 4) {
            sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            finish()
        }
    }
}

void onLoad() {
    sponge.enableJava(SampleJavaCorrelator)
}

void onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
}
