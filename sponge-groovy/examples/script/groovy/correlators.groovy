/**
 * Sponge Knowledge base
 * Using correlators
 */

import org.openksavi.sponge.examples.SampleJavaCorrelator

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaFinishCount", new AtomicInteger(0))
}

class SampleCorrelator extends Correlator {
    def eventLog = []

    void onConfigure() {
        this.events = ["filesystemFailure", "diskFailure"]
        this.maxInstances = 1
    }
    boolean onAcceptAsFirst(Event event) {
        return event.name == "filesystemFailure"
    }
    void onEvent(Event event) {
        this.eventLog << event
        this.logger.debug("{} - event: {}, log: {}", this.hashCode(), event.name, this.eventLog)
        //hardwareFailureScriptCount.incrementAndGet()
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if (this.eventLog.size() >= 4) {
            EPS.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            finish()
        }
    }
}

void onLoad() {
    EPS.enableJava(SampleJavaCorrelator)
}

void onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
}
