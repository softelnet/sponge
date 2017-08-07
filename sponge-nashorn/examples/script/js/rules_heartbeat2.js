/**
 * Sponge Knowledge base
 * Heartbeat 2
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    EPS.setVariable("soundTheAlarm", new AtomicBoolean(false));
}

// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
var HeartbeatRule = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["heartbeat h1", "heartbeat h2 :none"];
        self.duration = Duration.ofSeconds(2);
    },
    onRun: function(self, event) {
        self.logger.info("Sound the alarm!");
        EPS.getVariable("soundTheAlarm").set(true);
    }
});

function onStartup() {
    EPS.event("heartbeat").send();
    EPS.event("heartbeat").sendAfter(1000);
}
