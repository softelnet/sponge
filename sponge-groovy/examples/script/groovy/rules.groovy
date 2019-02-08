/**
 * Sponge Knowledge base
 * Using rules
 */

import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.examples.SameSourceJavaRule

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

class FirstRule extends Rule {
    void onConfigure() {
        // Events specified without aliases
        this.withEvents(["filesystemFailure", "diskFailure"]).withCondition("diskFailure", { rule, event ->
                return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0
        })
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
    }
}

class SameSourceAllRule extends Rule {
    void onConfigure() {
        // Events specified with aliases (e1 and e2)
        this.withEvents(["filesystemFailure e1", "diskFailure e2 :all"])
            .withCondition("e1", this.&severityCondition)
            .withConditions("e2", [this.&severityCondition, this.&diskFailureSourceCondition])
            .withDuration(Duration.ofSeconds(8))
    }
    void onRun(Event event) {
        this.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          this.eventSequence)
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    }
    boolean severityCondition(event) {
        return (event.get("severity") as int) > 5
    }
    boolean diskFailureSourceCondition(event) {
        // Both events have to have the same source
        Event event1 = this.getEvent("e1")
        return event.get("source") == event1.get("source") && Duration.between(event1.time, event.time).seconds <= 4
    }
}

void onLoad() {
    sponge.enableJava(SameSourceJavaRule)
}

void onStartup() {
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
}
