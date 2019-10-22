"""
Sponge Knowledge base
Rule builders
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("sameSourceFirstFireCount", AtomicInteger(0))

def onLoad():
    def firstRuleOnRun(rule, event):
        rule.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()

    sponge.enable(RuleBuilder("FirstRule").withEvents(["filesystemFailure", "diskFailure"]).withCondition("diskFailure",
                lambda rule, event: Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0)
                .withOnRun(firstRuleOnRun))

    def sameSourceAllRuleE2Condition(rule, event):
        # Both events have to have the same source
        event1 = rule.getEvent("e1")
        return event.get("source") == event1.get("source") and Duration.between(event1.time, event.time).seconds <= 4

    def sameSourceAllRuleOnRun(rule, event):
        rule.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          rule.eventSequence)
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()

    sponge.enable(RuleBuilder("SameSourceAllRule").withEvents(["filesystemFailure e1", "diskFailure e2 :all"])
                .withCondition("e1", lambda rule, event: int(event.get("severity")) > 5)
                .withCondition("e2", lambda rule, event: int(event.get("severity")) > 5)
                .withCondition("e2", sameSourceAllRuleE2Condition)
                .withDuration(Duration.ofSeconds(8)).withOnRun(sameSourceAllRuleOnRun))

def onStartup():
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
