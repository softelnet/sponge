/**
 * Sponge Knowledge base
 * Heartbeat
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

var hearbeatEventEntry;

function onInit() {
    hearbeatEventEntry = null;
    EPS.setVariable("soundTheAlarm", new AtomicBoolean(false));
}

var HeartbeatFilter = Java.extend(Filter, {
    configure: function(self) {
        self.event = "heartbeat";
    },
    init: function(self) {
        self.target = new function() {
            this.heartbeatCounter = 0;
        }
    },
    accepts: function(self, event) {
        self.target.heartbeatCounter++;
        if (self.target.heartbeatCounter > 2) {
            EPS.removeEvent(hearbeatEventEntry);
            return false;
        } else {
            return true;
        }
    }
});


// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
var HeartbeatRule = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["heartbeat h1", "heartbeat h2 :none"];
        self.duration = Duration.ofSeconds(2);
    },
    run: function(self, event) {
        EPS.event("alarm").set("severity", 1).send();
    }
});

var AlarmTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "alarm";
    },
    run: function(self, event) {
        print("Sound the alarm!");
        EPS.getVariable("soundTheAlarm").set(true);
    }
});

function onStartup() {
    hearbeatEventEntry = EPS.event("heartbeat").sendAfter(100, 1000);
}
