/**
 * Sponge Knowledge Base
 * Using filters for deduplication of events.
 */

var Deduplication = org.openksavi.sponge.core.library.Deduplication;
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    var eventCounter = java.util.Collections.synchronizedMap(new java.util.HashMap());
    eventCounter.put("e1-blue", new AtomicInteger(0))
    eventCounter.put("e1-red", new AtomicInteger(0))
    eventCounter.put("e2-blue", new AtomicInteger(0))
    eventCounter.put("e2-red", new AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter);
}

var ColorDeduplicationFilter = Java.extend(Filter, {
    onConfigure: function(self) {
        self.withEvent("e1");
    },
    onInit: function(self) {
        // There is some magic required here because of the limitations in JavaScript support.
        self.target = new function() {
            this.deduplication = new Deduplication("color");
        }
        self.target.deduplication.cacheBuilder.maximumSize(1000);
    },
    onAccept: function(self, event) {
        return self.target.deduplication.onAccept(event);
    }
});

var ColorTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2"]);
    },
    onRun: function(self, event) {
        self.logger.debug("Received event {}", event);
        sponge.getVariable("eventCounter").get(event.name + "-" + event.get("color")).incrementAndGet();
    }
});

function onStartup() {
    sponge.event("e1").set("color", "red").send();
    sponge.event("e1").set("color", "blue").send();
    sponge.event("e2").set("color", "red").send();
    sponge.event("e2").set("color", "blue").send();

    sponge.event("e1").set("color", "red").send();
    sponge.event("e1").set("color", "blue").send();
    sponge.event("e2").set("color", "red").send();
    sponge.event("e2").set("color", "blue").send();
}
