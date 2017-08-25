/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger1, file3";
        self.event = "e1";
    },
    onRun: function(self, event) {
        //self.logger.debug("file3: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var Trigger3 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger3, file3";
        self.event = "e3";
    },
    onRun: function(self, event) {
        //self.logger.debug("file3: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

// Execute immediately while loading
EPS.enableAll(Trigger1, Trigger3);

function onShutdown() {
    EPS.logger.debug("onShutdown, file3");
}
