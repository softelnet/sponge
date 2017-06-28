/**
 * Sponge Knowledge base
 * Removing scheduled events
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    EPS.setVariable("eventEntry", null);
    EPS.setVariable("eventCounter", new AtomicInteger(0));
    EPS.setVariable("allowNumber", 3);
}

var Trigger1 = Java.extend(Trigger, {
    configure: function(self) {
        self.eventName = "e1";
    },
    run: function(self, event) {
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
    configure: function(self) {
        self.eventName = "e2";
    },
    run: function(self, event) {
        self.logger.debug("Removing entry");
        EPS.removeEvent(EPS.getVariable("eventEntry"));
    }
});

function onStartup() {
    var start = 100;
    var interval = 500;
    EPS.setVariable("eventEntry", EPS.event("e1").sendAfter(start, interval));
    EPS.event("e2").sendAfter(interval * EPS.getVariable("allowNumber"));
}
