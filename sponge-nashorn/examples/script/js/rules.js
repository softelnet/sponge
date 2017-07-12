/**
 * Sponge Knowledge base
 * Using rules
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
    EPS.setVariable("sameSourceFirstFireCount", new AtomicInteger(0));
}

var FirstRule = Java.extend(Rule, {
    configure: function(self) {
        // Events specified without aliases
        self.events = ["filesystemFailure", "diskFailure"];
        self.setConditions("diskFailure", function(rule, event) {
            return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0;
        });
    },
    run: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        EPS.getVariable("sameSourceFirstFireCount").incrementAndGet();
    }
});

var SameSourceAllRule = Java.extend(Rule, {
    configure: function(self) {
        // Events specified with aliases (e1 and e2)
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"];
        self.setConditions("e1", this.severityCondition);
        self.setConditions("e2", this.severityCondition, function(rule, event) {
            // Both events have to have the same source
            event1 = rule.getEvent("e1");
            return event.get("source") == event1.get("source") &&
                Duration.between(event1.time, event.time).seconds <= 4;
        });
        self.duration = Duration.ofSeconds(8);
    },
    run: function(self, event) {
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          self.eventSequence);
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet();
    },
    severityCondition: function(self, event) {
        return parseInt(event.get("severity")) > 5;
    }
});

function onLoad() {
    EPS.enableJava(org.openksavi.sponge.examples.SameSourceJavaRule.class)
}

function onStartup() {
    EPS.event("filesystemFailure").set("severity", 8).set("source", "server1").send();
    EPS.event("diskFailure").set("severity", 10).set("source", "server1").send();
    EPS.event("diskFailure").set("severity", 10).set("source", "server2").send();
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send();
    EPS.event("diskFailure").set("severity", 8).set("source", "server1").send();
    EPS.event("diskFailure").set("severity", 1).set("source", "server1").send();
}

