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
    private eventLog

    void onConfigure() {
        this.events = ["filesystemFailure", "diskFailure"]
        this.duration = Duration.ofSeconds(2)
    }
    boolean onAcceptAsFirst(Event event) {
        return instanceStarted.compareAndSet(false, true)
    }
    void onInit() {
        eventLog = []
    }
    void onEvent(Event event) {
        eventLog << event
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    }
    void onDuration() {
        this.logger.debug("{} - event: {}, log: {}", this.hashCode(), event.name, eventLog)
    }
}

void onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
}
