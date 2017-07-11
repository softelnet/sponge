/**
 * Sponge Knowledge base
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
    EPS.setVariable("eventCounter", eventCounter);
}

var ColorDeduplicationFilter = Java.extend(Filter, {
    configure: function(self) {
        self.event = "e1";
    },
    init: function(self) {
        // There is some magic required here because of the limitations in JavaScript support.
        self.target = new function() {
            this.deduplication = new Deduplication("color");
        }
        self.target.deduplication.cacheBuilder.maximumSize(1000);
    },
    accepts: function(self, event) {
        return self.target.deduplication.accepts(event);
    }
});

var ColorTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.events = ["e1", "e2"];
    },
    run: function(self, event) {
        self.logger.debug("Received event {}", event);
        EPS.getVariable("eventCounter").get(event.name + "-" + event.get("color")).incrementAndGet();
    }
});

function onStartup() {
    EPS.event("e1").set("color", "red").sendAfter(100);
    EPS.event("e1").set("color", "blue").sendAfter(100);
    EPS.event("e2").set("color", "red").sendAfter(200);
    EPS.event("e2").set("color", "blue").sendAfter(200);

    EPS.event("e1").set("color", "red").sendAfter(300);
    EPS.event("e1").set("color", "blue").sendAfter(300);
    EPS.event("e2").set("color", "red").sendAfter(400);
    EPS.event("e2").set("color", "blue").sendAfter(400);
}
