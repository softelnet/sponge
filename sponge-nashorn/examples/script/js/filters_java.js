/**
 * Sponge Knowledge base
 * Using java filters 
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    var eventCounter = java.util.Collections.synchronizedMap(new java.util.HashMap());
    eventCounter.put("e1", new AtomicInteger(0));
    eventCounter.put("e2", new AtomicInteger(0));
    eventCounter.put("e3", new AtomicInteger(0));
    EPS.setVariable("eventCounter", eventCounter);
}

var FilterTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.setEvents("e1", "e2", "e3");
    },
    onRun: function(self, event) {
        self.logger.debug("Processing trigger for event {}", event);
        EPS.getVariable("eventCounter").get(event.name).incrementAndGet();
    }
});

function onLoad() {
    EPS.enableJava(org.openksavi.sponge.examples.ShapeFilter.class);
}

function onStartup() {
    EPS.event("e1").sendAfter(100, 100);
    EPS.event("e2").set("shape", "square").sendAfter(200, 100);
    EPS.event("e3").set("shape", "circle").sendAfter(300, 100);
}
