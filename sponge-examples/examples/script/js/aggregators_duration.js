/**
 * Sponge Knowledge base
 * Using aggregators duration
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
}

var SampleAggregator = Java.extend(Aggregator, {
    configure: function(self) {
        self.eventNames = ["filesystemFailure", "diskFailure"];
        self.duration = Duration.ofSeconds(2);
    },
    init: function(self) {
        self.target = new function() {
            this.eventLog = [];
        }
        EPS.setVariableIfNone("SampleAggregator_instanceStarted", function() { return new AtomicBoolean(false)});
    },
    acceptsAsFirst: function(self, event) {
        return EPS.getVariable("SampleAggregator_instanceStarted").compareAndSet(false, true);
    },
    onEvent: function(self, event) {
        self.target.eventLog.push(event);
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet();
    },
    onDuration: function(self) {
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, self.target.eventLog.toString());
    }
});

function onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100);
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100);
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100);
}