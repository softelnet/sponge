/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withLabel("Trigger1, file2").withEvent("e1");
    },
    onRun: function(self, event) {
        //self.logger.debug("file2: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.meta.label).incrementAndGet();
    }
});

var Trigger2 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withLabel("Trigger2, file2").withEvent("e2");
    },
    onRun: function(self, event) {
        //self.logger.debug("file2: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.meta.label).incrementAndGet();
    }
});

// Execute immediately while loading
sponge.enableAll(Trigger1, Trigger2);

function onShutdown() {
    sponge.logger.debug("onShutdown, file2");
}
