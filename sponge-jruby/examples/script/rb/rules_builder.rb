# Sponge Knowledge base
# Rule builders

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $sponge.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $sponge.setVariable("sameSourceFirstFireCount", AtomicInteger.new(0))
end

def onLoad
    $sponge.enable(RuleBuilder.new("FirstRule").withEvents(["filesystemFailure", "diskFailure"])
        .withCondition("diskFailure", lambda { |rule, event| Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0 })
        .withOnRun { |rule, event|
            rule.logger.debug("Running rule for event: {}", event.name)
            $sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
        })

    $sponge.enable(RuleBuilder.new("SameSourceAllRule").withEvents(["filesystemFailure e1", "diskFailure e2 :all"])
        .withCondition("e1", lambda { |rule, event| Integer(event.get("severity")) > 5 })
        .withCondition("e2", lambda { |rule, event| Integer(event.get("severity")) > 5 })
        .withCondition("e2", lambda { |rule, event|
            # Both events have to have the same source
            event1 = rule.getEvent("e1")
            event.get("source") == event1.get("source") && Duration.between(event1.time, event.time).seconds <= 4
         })
         .withDuration(Duration.ofSeconds(8)).withOnRun { |rule, event|
             rule.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          rule.eventSequence)
             $sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
         })
end

def onStartup
    $sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    $sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 1).set("source", "server1").send()
end