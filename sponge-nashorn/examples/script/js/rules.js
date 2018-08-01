/**
 * Sponge Knowledge base
 * Using rules
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0));
}

var FirstRule = Java.extend(Rule, {
    onConfigure: function(self) {
        // Events specified without aliases
        self.events = ["filesystemFailure", "diskFailure"];
        self.addConditions("diskFailure", function(rule, event) {
            return Duration.between(rule.getEvent("filesystemFailure").time, event.time).seconds >= 0;
        });
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet();
    }
});

var SameSourceAllRule = Java.extend(Rule, {
    onConfigure: function(self) {
        // Events specified with aliases (e1 and e2)
        self.events = ["filesystemFailure e1", "diskFailure e2 :all"];
        self.addConditions("e1", this.severityCondition);
        self.addConditions("e2", this.severityCondition, function(rule, event) {
            // Both events have to have the same source
            event1 = rule.getEvent("e1");
            return event.get("source") == event1.get("source") &&
                Duration.between(event1.time, event.time).seconds <= 4;
        });
        self.duration = Duration.ofSeconds(8);
    },
    onRun: function(self, event) {
        self.logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event.time, event.get("source"),
                                                                                          self.eventSequence);
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet();
    },
    severityCondition: function(self, event) {
        return parseInt(event.get("severity")) > 5;
    }
});

function onLoad() {
    sponge.enableJava(org.openksavi.sponge.examples.SameSourceJavaRule.class)
}

function onStartup() {
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send();
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 1).set("source", "server1").send();
}

