/*
 * Sponge Knowledge base
 * Using rules
 */

import org.openksavi.sponge.examples.SameSourceJavaRule
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    EPS.setVariable("sameSourceFirstFireCount", AtomicInteger(0))
}

class FirstRule : Rule() {
    override fun onConfigure() {
        // Events specified without aliases
        setEvents("filesystemFailure", "diskFailure")
        addConditions("diskFailure", { rule: Rule, event: Event ->
            Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0
        })
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for event: {}", event?.name)
        eps.getVariable<AtomicInteger>("sameSourceFirstFireCount").incrementAndGet()
    }
}

class SameSourceAllRule : Rule() {
    override fun onConfigure() {
        // Events specified with aliases (e1 and e2)
        setEvents("filesystemFailure e1", "diskFailure e2 :all")
        addCondition("e1", this::severityCondition)
        addConditions("e2", this::severityCondition, this::diskFailureSourceCondition)
        duration = Duration.ofSeconds(8)
    }

    override fun onRun(event: Event?) {
        logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event?.time, event?.get("source"),
                eventSequence)
        eps.getVariable<AtomicInteger>("hardwareFailureScriptCount").incrementAndGet()
    }

    fun severityCondition(event: Event) = event.get<Int>("severity") > 5

    fun diskFailureSourceCondition(event: Event): Boolean {
        // Both events have to have the same source
        val event1 = this.getEvent("e1")
        return event.get<Any>("source") == event1.get<Any>("source") && Duration.between(event1.time, event.time).seconds <= 4
    }
}

fun onLoad() = EPS.enableJava(SameSourceJavaRule::class.java)

fun onStartup() {
    EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 10).set("source", "server2").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    EPS.event("diskFailure").set("severity", 1).set("source", "server1").send()
}
