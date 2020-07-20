/**
 * Sponge Knowledge Base
 * Using filter builders
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger

function onInit() {
    // Variables for assertions only
    var eventCounter = java.util.Collections.synchronizedMap(new java.util.HashMap())
    eventCounter.put("blue", new AtomicInteger(0))
    eventCounter.put("red", new AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

var ColorTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("e1")
    },
    onRun: function(self, event) {
        self.logger.debug("Received event {}", event)
        sponge.getVariable("eventCounter").get(event.get("color")).incrementAndGet()
    }
});

function onLoad() {
    sponge.enable(new FilterBuilder("ColorFilter").withEvent("e1").withOnAccept(function (filter, event) {
        sponge.logger.debug("Received event {}", event)
        var color = event.get("color", null)
        if (color == null || color != "blue") {
            sponge.logger.debug("rejected")
            return false
        } else {
            sponge.logger.debug("accepted")
            return true
        }
    }))
}

function onStartup() {
    sponge.event("e1").send()
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
}
