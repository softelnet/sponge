/**
 * Sponge Knowledge base
 * Using aggregators duration
 */

import org.openksavi.sponge.examples.SampleJavaAggregator

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
}

class SampleAggregator extends Aggregator {
    static AtomicBoolean instanceStarted = new AtomicBoolean(false)
    def eventLog = []

    void configure() {
        this.eventNames = ["filesystemFailure", "diskFailure"]
        this.duration = Duration.ofSeconds(2)
    }
    boolean acceptsAsFirst(Event event) {
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
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
}
