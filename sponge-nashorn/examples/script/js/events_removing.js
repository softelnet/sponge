/**
 * Sponge Knowledge base
 * Removing scheduled events
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    sponge.setVariable("eventEntry", null);
    sponge.setVariable("eventCounter", new AtomicInteger(0));
    sponge.setVariable("allowNumber", 2);
}

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onRun: function(self, event) {
        eventCounter = sponge.getVariable("eventCounter")
        eventCounter++;
        sponge.setVariable("eventCounter", eventCounter);
        self.logger.debug("Received event {}, counter: {}", event.name, eventCounter);
        if (eventCounter > sponge.getVariable("allowNumber")) {
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
        sponge.removeEvent(sponge.getVariable("eventEntry"));
    }
});

function onStartup() {
    var start = 500;
    var interval = 1000;
    sponge.setVariable("eventEntry", sponge.event("e1").sendAfter(start, interval));
    sponge.event("e2").sendAfter(interval * sponge.getVariable("allowNumber"));
}
