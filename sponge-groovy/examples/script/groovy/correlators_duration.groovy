/**
 * Sponge Knowledge base
 * Using correlator duration
 */

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
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
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    }
    void onDuration() {
        this.logger.debug("{} - log: {}", this.hashCode(), eventLog)
    }
}

void onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
}
