/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withLabel("Trigger1, file3").withEvent("e1");
    },
    onRun: function(self, event) {
        sponge.getVariable("eventCounter").get(self.meta.label).incrementAndGet();
    }
});

var Trigger3 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withLabel("Trigger3, file3").withEvent("e3");
    },
    onRun: function(self, event) {
        sponge.getVariable("eventCounter").get(self.meta.label).incrementAndGet();
    }
});

// Execute immediately while loading
sponge.enableAll(Trigger1, Trigger3);

function onShutdown() {
    sponge.logger.debug("onShutdown, file3");
}
