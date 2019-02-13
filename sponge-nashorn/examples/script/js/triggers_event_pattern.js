/**
 * Sponge Knowledge base
 * Triggers - Event pattern
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("countA", new AtomicInteger(0));
    sponge.setVariable("countAPattern", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a");
    },
    onRun: function(self, event) {
        sponge.getVariable("countA").incrementAndGet();
    }
});

var TriggerAPattern = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a.*");
    },
    onRun: function(self, event) {
        self.logger.debug("Received matching event {}", event.name);
        sponge.getVariable("countAPattern").incrementAndGet();
    }
});

function onStartup() {
    ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ].forEach(function(name) {
        sponge.event(name).send();
    });
}
