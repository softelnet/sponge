/**
 * Sponge Knowledge base
 * Using unordered rules
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var Deduplication = org.openksavi.sponge.core.library.Deduplication;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
    sponge.setVariable("sameSourceFirstFireCount", new AtomicInteger(0));
}

var FirstRule = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["filesystemFailure", "diskFailure"]).withOrdered(false);
        self.withAllConditions([
            function(rule, event) { return rule.firstEvent.get("source") == event.get("source"); },
            function(rule, event) { return Duration.between(rule.firstEvent.time, event.time).seconds <= 2; }
        ]);
        self.withDuration(Duration.ofSeconds(5));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventSequence);
        sponge.getVariable("sameSourceFirstFireCount").incrementAndGet();
        sponge.event("alarm").set("source", self.firstEvent.get("source")).send();
    }
});

var SameSourceAllRule = Java.extend(Rule, {
    onConfigure: function(self) {
        // Events specified with aliases (e1 and e2)
        self.withEvents(["filesystemFailure e1", "diskFailure e2 :all"]).withOrdered(false);
        self.withCondition("e1", this.severityCondition);
        self.withConditions("e2", [
            this.severityCondition,
            function(rule, event) {
                return event.get("source") == rule.firstEvent.get("source") &&
                    Duration.between(rule.firstEvent.time, event.time).seconds <= 4;
            }
        ]);
        self.withDuration(Duration.ofSeconds(8));
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

var AlarmFilter = Java.extend(Filter, {
    onConfigure: function(self) {
        self.withEvent("alarm");
    },
    onInit: function(self) {
        // There is some magic required here because of the limitations in JavaScript support.
        self.target = new function() {
            this.deduplication = new Deduplication("source");
        }
        self.target.deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit.SECONDS);
    },
    onAccept: function(self, event) {
        return self.target.deduplication.onAccept(event);
    }
});

var Alarm = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("alarm");
    },
    onRun: function(self, event) {
        self.logger.debug("Received alarm from {}", event.get("source"));
    }
});

function onLoad() {
    sponge.enableJava(org.openksavi.sponge.examples.SameSourceJavaUnorderedRule.class);
}

function onStartup() {
    sponge.event("diskFailure").set("severity", 10).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 10).set("source", "server2").send();
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send();
    sponge.event("filesystemFailure").set("severity", 6).set("source", "server1").send();
    sponge.event("diskFailure").set("severity", 6).set("source", "server1").send();
}
