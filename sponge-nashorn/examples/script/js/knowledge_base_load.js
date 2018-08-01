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
    sponge.setVariable("eventCounter", eventCounter);

}
var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger1, file1";
        self.event = "e1";
    },
    onRun: function(self, event) {
        //self.logger.debug("file1: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var Trigger2 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger2, file1";
        self.event = "e2";
    },
    onRun: function(self, event) {
        //self.logger.debug("file1: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var LoadKbFile = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "loadKbFile";
    },
    onRun: function(self, event) {
        var kbFile = event.get("kbFile");
        sponge.kb.load(kbFile);
        self.logger.info("File {} loaded", kbFile);
    }
});

function onLoad() {
    sponge.enableAll(Trigger1, Trigger2, LoadKbFile);
}

function onStartup() {
    sponge.logger.debug("onStartup, file1: {}, triggers: {}", sponge.info, sponge.engine.triggers);
    sponge.event("e1").sendAfter(0, 500);
    sponge.event("e2").sendAfter(0, 500);

    sponge.event("loadKbFile").set("kbFile", "examples/script/js/knowledge_base_load2.js").sendAfter(2000);
    sponge.event("loadKbFile").set("kbFile", "examples/script/js/knowledge_base_load3.js").sendAfter(5000);
}

function onShutdown() {
    sponge.logger.debug("onShutdown, file1");
}
