/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

var Trigger1 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger1, file2";
        self.event = "e1";
    },
    onRun: function(self, event) {
        self.logger.debug("file2: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

var Trigger2 = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.displayName = "Trigger2, file2";
        self.event = "e2";
    },
    onRun: function(self, event) {
        self.logger.debug("file2: Received event {}", event);
        EPS.getVariable("eventCounter").get(self.displayName).incrementAndGet();
    }
});

// Execute immediately while loading
EPS.enableAll(Trigger1, Trigger2);

function onShutdown() {
    EPS.logger.debug("onShutdown, file2");
}
