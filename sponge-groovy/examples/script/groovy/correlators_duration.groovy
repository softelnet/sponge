/**
 * Sponge Knowledge base
 * Using correlator duration
 */

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
}

class SampleCorrelator extends Correlator {
    static AtomicBoolean instanceStarted = new AtomicBoolean(false)
    def eventLog = []

    void onConfigure() {
        this.events = ["filesystemFailure", "diskFailure"]
        this.duration = Duration.ofSeconds(2)
    }
    boolean onAcceptAsFirst(Event event) {
        return instanceStarted.compareAndSet(false, true)
    }
    void onEvent(Event event) {
        this.eventLog << event
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    }
    void onDuration() {
        this.logger.debug("{} - event: {}, log: {}", this.hashCode(), event.name, this.eventLog)
    }
}

void onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
}
