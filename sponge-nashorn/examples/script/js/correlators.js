/**
 * Sponge Knowledge Base
 * Using correlators
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
    sponge.setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
    sponge.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0));
    sponge.setVariable("hardwareFailureJavaFinishCount", new AtomicInteger(0));
}

var SampleCorrelator = Java.extend(Correlator, {
    onConfigure: function(self) {
        self.withEvents(["filesystemFailure", "diskFailure"]).withMaxInstances(1);
    },
    onAcceptAsFirst: function(self, event) {
        return event.name == "filesystemFailure"
    },
    onInit: function(self) {
        self.target = new function() {
            this.eventLog = [];
        }
    },
    onEvent: function(self, event) {
        self.target.eventLog.push(event);
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, self.target.eventLog.toString());
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet();
        if (self.target.eventLog.length >= 4) {
            sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet();
            self.finish();
        }
    }
});

function onLoad() {
    sponge.enableJava(org.openksavi.sponge.examples.SampleJavaCorrelator.class);
}

function onStartup() {
    sponge.event("filesystemFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server2").send();
    sponge.event("diskFailure").set("source", "server1").send();
    sponge.event("diskFailure").set("source", "server2").send();
}