/**
 * Sponge Knowledge base
 * Using filters
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    var eventCounter = java.util.Collections.synchronizedMap(new java.util.HashMap());
    eventCounter.put("blue", new AtomicInteger(0));
    eventCounter.put("red", new AtomicInteger(0));
    sponge.setVariable("eventCounter", eventCounter);
}

var ColorFilter = Java.extend(Filter, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onAccept: function(self, event) {
        self.logger.debug("Received event {}", event);
        var color = event.get("color", null);
        if (color == null || color != "blue") {
            self.logger.debug("rejected");
            return false;
        } else {
            self.logger.debug("accepted");
            return true;
        }
    }
});

var ColorTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onRun: function(self, event) {
        self.logger.debug("Received event {}", event);
        sponge.getVariable("eventCounter").get(event.get("color")).incrementAndGet();
    }
});

function onStartup() {
    sponge.event("e1").send();
    sponge.event("e1").set("color", "red").send();
    sponge.event("e1").set("color", "blue").send();
}
