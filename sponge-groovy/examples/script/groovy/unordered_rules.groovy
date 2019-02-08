/**
 * Sponge Knowledge base
 * Using unordered rules
 */

import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.examples.SameSourceJavaUnorderedRule
import org.openksavi.sponge.core.library.Deduplication

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

class FirstRule extends Rule {
    void onConfigure() {
        this.withEvents(["filesystemFailure", "diskFailure"]).withOrdered(false).withAllConditions([
            { rule, event -> return rule.firstEvent.get("source") == event.get("source")},
            { rule, event -> return Duration.between(rule.firstEvent.time, event.time).seconds <= 2}
        ]).withDuration(Duration.ofSeconds(5))
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventSequence)
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
        sponge.event("alarm").set("source", this.firstEvent.get("source")).send()
    }
}

class SameSourceAllRule extends Rule {
    void onConfigure() {
        this.withEvents(["filesystemFailure e1", "diskFailure e2 :all"]).withOrdered(false)
            .withCondition("e1", this.&severityCondition).withConditions("e2", [this.&severityCondition, this.&diskFailureSourceCondition])
            .withDuration(Duration.ofSeconds(5))
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
        return event.get("source") == this.firstEvent.get("source") && Duration.between(this.firstEvent.time, event.time).seconds <= 4
    }
}

class AlarmFilter extends Filter {
    def deduplication = new Deduplication("source")
    void onConfigure() {
        this.withEvent("alarm")
    }
    void onInit() {
        this.deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit.SECONDS)
    }
    boolean onAccept(Event event) {
        return this.deduplication.onAccept(event)
    }
}

class Alarm extends Trigger {
    void onConfigure() {
        this.withEvent("alarm")
    }
    void onRun(Event event) {
        this.logger.debug("Received alarm from {}", event.get("source"))
    }
}

void onLoad() {
    sponge.enableJava(SameSourceJavaUnorderedRule)
}

void onStartup() {
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 6).set("source", "server1").send()
}
