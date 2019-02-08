/**
 * Sponge Knowledge base
 * Using correlator duration
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
}

var SampleCorrelator = Java.extend(Correlator, {
    onConfigure: function(self) {
        self.withEvents(["filesystemFailure", "diskFailure"]).withDuration(Duration.ofSeconds(2));
        sponge.setVariableIfNone("SampleCorrelator_instanceStarted", function() { return new AtomicBoolean(false)});
    },
    onAcceptAsFirst: function(self, event) {
        return sponge.getVariable("SampleCorrelator_instanceStarted").compareAndSet(false, true);
    },
    onInit: function(self) {
        self.target = new function() {
            this.eventLog = [];
        }
    },
    onEvent: function(self, event) {
        self.target.eventLog.push(event);
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet();
    },
    onDuration: function(self) {
        self.logger.debug("{} - log: {}", self.hashCode(), self.target.eventLog.toString());
    }
});

function onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server1").sendAfter(200, 100);
    sponge.event("diskFailure").set("source", "server2").sendAfter(200, 100);
}