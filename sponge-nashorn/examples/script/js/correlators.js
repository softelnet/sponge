/**
 * Sponge Knowledge base
 * Using correlators
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", new AtomicInteger(0));
    EPS.setVariable("hardwareFailureJavaCount", new AtomicInteger(0));
    EPS.setVariable("hardwareFailureScriptFinishCount", new AtomicInteger(0));
    EPS.setVariable("hardwareFailureJavaFinishCount", new AtomicInteger(0));
}

var SampleCorrelator = Java.extend(Correlator, {
    configure: function(self) {
        self.events = ["filesystemFailure", "diskFailure"];
        self.maxInstances = 1;
    },
    init: function(self) {
        self.target = new function() {
            this.eventLog = [];
        }
    },
    onEvent: function(self, event) {
        self.target.eventLog.push(event);
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, self.target.eventLog.toString());
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet();
        if (self.target.eventLog.length >= 4) {
            EPS.getVariable("hardwareFailureScriptFinishCount").incrementAndGet();
            self.finish();
        }
    }
});

function onLoad() {
    EPS.enableJava(org.openksavi.sponge.examples.SampleJavaCorrelator.class);
}

function onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100);
    EPS.event("diskFailure").set("source", "server1").sendAfter(200);
    EPS.event("diskFailure").set("source", "server2").sendAfter(200);
    EPS.event("diskFailure").set("source", "server1").sendAfter(300);
    EPS.event("diskFailure").set("source", "server2").sendAfter(300);
}