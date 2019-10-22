/*
 * Sponge Knowledge base
 * Rule builders
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", AtomicInteger(0))
}

fun onLoad() {
        sponge.enable(KRuleBuilder("FirstRule").withEvents("filesystemFailure", "diskFailure")
            .withCondition("diskFailure", 
                { rule, event -> Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0})
            .withOnRun({ rule, event ->
                rule.logger.debug("Running rule for event: {}", event?.name)
                sponge.getVariable<AtomicInteger>("sameSourceFirstFireCount").incrementAndGet()
            }))

        sponge.enable(KRuleBuilder("SameSourceAllRule").withEvents("filesystemFailure e1", "diskFailure e2 :all")
            .withCondition("e1", { rule, event -> event.get<Int>("severity") > 5 })
            .withCondition("e2", { rule, event -> event.get<Int>("severity") > 5 })
            .withCondition("e2", { rule, event ->
                // Both events have to have the same source
                val event1 = rule.getEvent("e1")
                event.get<Any>("source") == event1.get<Any>("source") && Duration.between(event1.time, event.time).seconds <= 4
             })
             .withDuration(Duration.ofSeconds(8)).withOnRun({ rule, event ->
                logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event?.time, event?.get("source"),
                    rule.eventSequence)
                sponge.getVariable<AtomicInteger>("hardwareFailureScriptCount").incrementAndGet()
             }))
    }

fun onStartup() {
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
}
