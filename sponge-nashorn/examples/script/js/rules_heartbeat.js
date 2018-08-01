/**
 * Sponge Knowledge base
 * Heartbeat
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

var hearbeatEventEntry;

function onInit() {
    hearbeatEventEntry = null;
    sponge.setVariable("soundTheAlarm", new AtomicBoolean(false));
}

var HeartbeatFilter = Java.extend(Filter, {
    onConfigure: function(self) {
        self.event = "heartbeat";
    },
    onInit: function(self) {
        self.target = new function() {
            this.heartbeatCounter = 0;
        }
    },
    onAccept: function(self, event) {
        self.target.heartbeatCounter++;
        if (self.target.heartbeatCounter > 2) {
            sponge.removeEvent(hearbeatEventEntry);
            return false;
        } else {
            return true;
        }
    }
});


// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
var HeartbeatRule = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["heartbeat h1", "heartbeat h2 :none"];
        self.addConditions("h2", function(rule, event) {
            return rule.firstEvent.get("source") == event.get("source");
        });
        self.duration = Duration.ofSeconds(2);
    },
    onRun: function(self, event) {
        sponge.event("alarm").set("severity", 1).send();
    }
});

var AlarmTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "alarm";
    },
    onRun: function(self, event) {
        print("Sound the alarm!");
        sponge.getVariable("soundTheAlarm").set(true);
    }
});

function onStartup() {
    hearbeatEventEntry = sponge.event("heartbeat").set("source", "Host1").sendAfter(100, 1000);
}
