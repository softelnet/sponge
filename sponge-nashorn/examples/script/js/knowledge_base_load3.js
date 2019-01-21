/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.label = "Trigger1, file3";
        self.event = "e1";
    },
    onRun: function(self, event) {
        //self.logger.debug("file3: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.label).incrementAndGet();
    }
});

var Trigger3 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.label = "Trigger3, file3";
        self.event = "e3";
    },
    onRun: function(self, event) {
        //self.logger.debug("file3: Received event {}", event);
        sponge.getVariable("eventCounter").get(self.label).incrementAndGet();
    }
});

// Execute immediately while loading
sponge.enableAll(Trigger1, Trigger3);

function onShutdown() {
    sponge.logger.debug("onShutdown, file3");
}
