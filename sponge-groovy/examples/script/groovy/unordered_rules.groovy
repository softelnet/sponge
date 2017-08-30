/**
 * Sponge Knowledge base
 * Using unordered rules
 */

import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.examples.SameSourceJavaUnorderedRule
import org.openksavi.sponge.core.library.Deduplication

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    EPS.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

class FirstRule extends Rule {
    void onConfigure() {
        this.events = ["filesystemFailure", "diskFailure"]; this.ordered = false
        this.addAllConditions({ rule, event -> return rule.firstEvent.get("source") == event.get("source")})
        this.addAllConditions({ rule, event -> return Duration.between(rule.firstEvent.time, event.time).seconds <= 2})
        this.duration = Duration.ofSeconds(5)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventSequence)
        EPS.getVariable("sameSourceFirstFireCount").incrementAndGet()
        EPS.event("alarm").set("source", this.firstEvent.get("source")).send()
    }
}

class SameSourceAllRule extends Rule {
    void onConfigure() {
        this.events = ["filesystemFailure e1", "diskFailure e2 :all"]; this.ordered = false
        this.addConditions("e1", this.&severityCondition)
        this.addConditions("e2", this.&severityCondition, this.&diskFailureSourceCondition)
        this.duration = Duration.ofSeconds(5)
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
        return event.get("source") == this.firstEvent.get("source") && Duration.between(this.firstEvent.time, event.time).seconds <= 4
    }
}

class AlarmFilter extends Filter {
    def deduplication = new Deduplication("source")
    void onConfigure() {
        this.event = "alarm"
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
        this.event = "alarm"
    }
    void onRun(Event event) {
        this.logger.debug("Received alarm from {}", event.get("source"))
    }
}

void onLoad() {
    EPS.enableJava(SameSourceJavaUnorderedRule)
}

void onStartup() {
    EPS.event("diskFailure").set("severity", 10).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server2").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 6).set("source", "server1").send()
}
