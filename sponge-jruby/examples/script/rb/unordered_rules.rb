# Sponge Knowledge base
# Using unordered rules

java_import java.util.concurrent.atomic.AtomicInteger
java_import org.openksavi.sponge.examples.SameSourceJavaUnorderedRule
java_import org.openksavi.sponge.core.library.Deduplication

def onInit
    # Variables for assertions only
    $sponge.setVariable("hardwareFailureJavaCount", AtomicInteger.new(0))
    $sponge.setVariable("hardwareFailureScriptCount", AtomicInteger.new(0))
    $sponge.setVariable("sameSourceFirstFireCount", AtomicInteger.new(0))
end

class FirstRule < Rule
    def onConfigure
        self.events = ["filesystemFailure", "diskFailure"]; self.ordered = false
        self.addAllConditions(lambda { |rule, event| rule.firstEvent.get("source") == event.get("source")})
        self.addAllConditions(lambda { |rule, event|
            Duration.between(rule.firstEvent.time, event.time).seconds <= 2})
        self.duration = Duration.ofSeconds(5)
    end
    def onRun(event)
        self.logger.debug("Running rule for events: {}", self.eventSequence)
        $sponge.getVariable("sameSourceFirstFireCount").incrementAndGet()
        $sponge.event("alarm").set("source", self.firstEvent.get("source")).send()
    end
end

class SameSourceAllRule < Rule
    def onConfigure
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"]; self.ordered = false
        self.addConditions("e1", self.method(:severityCondition))
        self.addConditions("e2", self.method(:severityCondition), self.method(:diskFailureSourceCondition))
        self.duration = Duration.ofSeconds(5)
    end
    def onRun(event)
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                         self.eventSequence)
        $sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    end
    def severityCondition(event)
        return Integer(event.get("severity")) > 5
    end
    def diskFailureSourceCondition(event)
        return event.get("source") == self.firstEvent.get("source") && Duration.between(self.firstEvent.time, event.time).seconds <= 4
    end
end

class AlarmFilter < Filter
    def onConfigure
        self.event = "alarm"
    end
    def onInit
        @deduplication = Deduplication.new("source")
        @deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit::SECONDS)
    end
    def onAccept(event)
        return @deduplication.onAccept(event)
    end
end

class Alarm < Trigger
    def onConfigure
        self.event = "alarm"
    end
    def onRun(event)
        self.logger.debug("Received alarm from {}", event.get("source"))
    end
end

def onLoad
    $sponge.enableJava(SameSourceJavaUnorderedRule)
end

def onStartup
    $sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
    $sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
    $sponge.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
    $sponge.event("diskFailure").set("severity", 6).set("source", "server1").send()
end
