/**
 * Sponge Knowledge base
 * Removing scheduled events
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    EPS.setVariable("eventEntry", null);
    EPS.setVariable("eventCounter", new AtomicInteger(0));
    EPS.setVariable("allowNumber", 2);
}

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onRun: function(self, event) {
        eventCounter = EPS.getVariable("eventCounter")
        eventCounter++;
        EPS.setVariable("eventCounter", eventCounter);
        self.logger.debug("Received event {}, counter: {}", event.name, eventCounter);
        if (eventCounter > EPS.getVariable("allowNumber")) {
        	self.logger.debug("This line should not be displayed!");
        }
    }
});

var Trigger2 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "e2";
    },
    onRun: function(self, event) {
        self.logger.debug("Removing entry");
        EPS.removeEvent(EPS.getVariable("eventEntry"));
    }
});

function onStartup() {
    var start = 500;
    var interval = 1000;
    EPS.setVariable("eventEntry", EPS.event("e1").sendAfter(start, interval));
    EPS.event("e2").sendAfter(interval * EPS.getVariable("allowNumber"));
}
