/**
 * Sponge Knowledge base
 * Using rules
 */

import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.examples.SameSourceJavaRule

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    EPS.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

class FirstRule extends Rule {
    void onConfigure() {
        // Events specified without aliases
        this.events = ["filesystemFailure", "diskFailure"]
        this.addConditions("diskFailure", { rule, event ->
                return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0
        })
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("sameSourceFirstFireCount").incrementAndGet()
    }
}

class SameSourceAllRule extends Rule {
    void onConfigure() {
        // Events specified with aliases (e1 and e2)
        this.events = ["filesystemFailure e1", "diskFailure e2 :all"]
        this.addConditions("e1", this.&severityCondition)
        this.addConditions("e2", this.&severityCondition, this.&diskFailureSourceCondition)
        this.duration = Duration.ofSeconds(8)
    }
    void onRun(Event event) {
        this.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          this.eventSequence)
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
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
    EPS.enableJava(SameSourceJavaRule)
}

void onStartup() {
    EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server2").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 1).set("source", "server1").send()
}
