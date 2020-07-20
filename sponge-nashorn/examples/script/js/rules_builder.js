/**
 * Sponge Knowledge Base
 * Rule builders
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0))
}

function onLoad() {
    sponge.enable(new RuleBuilder("FirstRule").withEvents(["filesystemFailure", "diskFailure"])
            .withCondition("diskFailure", function(rule, event) { 
                return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0
            }).withOnRun(function(rule, event) {
                rule.logger.debug("Running rule for event: {}", event.name)
                sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
            }))

    sponge.enable(new RuleBuilder("SameSourceAllRule").withEvents(["filesystemFailure e1", "diskFailure e2 :all"])
            .withCondition("e1", function(rule, event) { return parseInt(event.get("severity")) > 5 })
            .withCondition("e2", function(rule, event) { return parseInt(event.get("severity")) > 5 })
            .withCondition("e2", function(rule, event) {
                // Both events have to have the same source
                event1 = rule.getEvent("e1")
                return event.get("source") == event1.get("source") && Duration.between(event1.time, event.time).seconds <= 4
             })
             .withDuration(Duration.ofSeconds(8)).withOnRun(function(rule, event) {
                 rule.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                              rule.eventSequence)
                 sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
             }))
}

function onStartup() {
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
}

