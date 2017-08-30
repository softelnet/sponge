# Sponge Knowledge base
# Using rules

java_import java.util.concurrent.atomic.AtomicInteger
java_import org.openksavi.sponge.examples.SameSourceJavaRule

def onInit
    # Variables for assertions only
    $EPS.setVariable("hardwareFailureJavaCount", AtomicInteger.new(0))
    $EPS.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $EPS.setVariable("sameSourceFirstFireCount", AtomicInteger.new(0))
end

class FirstRule < Rule
    def onConfigure
        # Events specified without aliases
        self.events = ["filesystemFailure", "diskFailure"]
        self.addConditions("diskFailure", lambda { |rule, event|
            return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0
        })
    end
    def onRun(event)
        self.logger.debug("Running rule for event: {}", event.name)
        $EPS.getVariable("sameSourceFirstFireCount").incrementAndGet()
    end
end

class SameSourceAllRule < Rule
    def onConfigure
        # Events specified with aliases (e1 and e2)
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"]
        self.addConditions("e1", self.method(:severityCondition))
        self.addConditions("e2", self.method(:severityCondition), self.method(:diskFailureSourceCondition))
        self.duration = Duration.ofSeconds(8)
    end
    def onRun(event)
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          self.eventSequence)
        $EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    end
    def severityCondition(event)
        return Integer(event.get("severity")) > 5
    end
    def diskFailureSourceCondition(event)
        # Both events have to have the same source
        event1 = self.getEvent("e1")
        return event.get("source") == event1.get("source") && Duration.between(event1.time, event.time).seconds <= 4
    end
end

def onLoad
    $EPS.enableJava(SameSourceJavaRule)
end

def onStartup
    $EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    $EPS.event("diskFailure").set("severity", 10).set("source", "server1").send()
    $EPS.event("diskFailure").set("severity", 10).set("source", "server2").send()
    $EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $EPS.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $EPS.event("diskFailure").set("severity", 1).set("source", "server1").send()
end