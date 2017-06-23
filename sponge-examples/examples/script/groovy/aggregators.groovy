/**
 * Sponge Knowledge base
 * Using aggregators
 */

import org.openksavi.sponge.examples.SampleJavaAggregator

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
}

class SampleAggregator extends Aggregator {
    static AtomicBoolean instanceStarted = new AtomicBoolean(false)
    def eventLog = []

    void configure() {
        this.eventNames = ["filesystemFailure", "diskFailure"]
    }
    boolean acceptsAsFirst(Event event) {
        return instanceStarted.compareAndSet(false, true)
    }
    void onEvent(Event event) {
        this.eventLog << event
        this.logger.debug("{} - event: {}, log: {}", this.hashCode(), event.name, this.eventLog)
        //hardwareFailureScriptCount.incrementAndGet()
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if (this.eventLog.size() >= 4) {
            finish()
        }
    }
}

void onLoad() {
    EPS.enableJava(SampleJavaAggregator)
}

void onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
}
