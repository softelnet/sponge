/**
 * Sponge Knowledge base
 * Rule builders
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

void onLoad() {
    sponge.enable(new RuleBuilder("FirstRule").withEvents(["filesystemFailure", "diskFailure"])
        .withCondition("diskFailure", { rule, event -> Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0})
        .withOnRun({ rule, event ->
            rule.logger.debug("Running rule for event: {}", event.name)
            sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
        }))

    sponge.enable(new RuleBuilder("SameSourceAllRule").withEvents(["filesystemFailure e1", "diskFailure e2 :all"])
        .withCondition("e1", { rule, event -> (event.get("severity") as int) > 5 })
        .withCondition("e2", { rule, event -> (event.get("severity") as int) > 5 })
        .withCondition("e2", { rule, event ->
            // Both events have to have the same source
            Event event1 = rule.getEvent("e1")
            event.get("source") == event1.get("source") && Duration.between(event1.time, event.time).seconds <= 4
         })
         .withDuration(Duration.ofSeconds(8)).withOnRun({ rule, event ->
             rule.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          rule.eventSequence)
             sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
         }))
}

void onStartup() {
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
}
