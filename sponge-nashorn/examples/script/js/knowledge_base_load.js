/**
 * Sponge Knowledge base
 * Loading knowledge bases
 * Note that auto-enable is turned off in the configuration.
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

var eventCounter;

function onInit() {
    // Variables for assertions only
    eventCounter = java.util.Collections.synchronizedMap(new java.util.HashMap());
    eventCounter.put("Trigger1, file1", new AtomicInteger(0));
    eventCounter.put("Trigger2, file1", new AtomicInteger(0));
    eventCounter.put("Trigger1, file2", new AtomicInteger(0));
    eventCounter.put("Trigger2, file2", new AtomicInteger(0));
    eventCounter.put("Trigger1, file3", new AtomicInteger(0));
    eventCounter.put("Trigger3, file3", new AtomicInteger(0));
    EPS.setVariable("eventCounter", eventCounter);

}
var Trigger1 = Java.extend(Trigger, {
    configure: function(self) {
        self.displayName = "Trigger1, file1";
        self.event = "e1";
    },
    run: function(self, event) {
        self.logger.debug("file1: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var Trigger2 = Java.extend(Trigger, {
    configure: function(self) {
        self.displayName = "Trigger2, file1";
        self.event = "e2";
    },
    run: function(self, event) {
        self.logger.debug("file1: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var LoadKbFile = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "loadKbFile";
    },
    run: function(self, event) {
        var kbFile = event.get("kbFile");
        EPS.kb.load(kbFile);
        self.logger.info("File {} loaded", kbFile);
    }
});

function onLoad() {
    EPS.enableAll(Trigger1, Trigger2, LoadKbFile);
}

function onStartup() {
    EPS.logger.debug("onStartup, file1: {}, triggers: {}", EPS.description, EPS.engine.triggers);
    EPS.event("e1").sendAfter(0, 100);
    EPS.event("e2").sendAfter(0, 100);

    EPS.event("loadKbFile").set("kbFile", "examples/script/js/knowledge_base_load2.js").sendAfter(500);
    EPS.event("loadKbFile").set("kbFile", "examples/script/js/knowledge_base_load3.js").sendAfter(1500);
}

function onShutdown() {
    EPS.logger.debug("onShutdown, file1");
}
