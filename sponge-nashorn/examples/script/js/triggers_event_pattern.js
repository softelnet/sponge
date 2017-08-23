/**
 * Sponge Knowledge base
 * Triggers - Event pattern
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("countA", new AtomicInteger(0));
    EPS.setVariable("countAPattern", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "a";
    },
    onRun: function(self, event) {
        EPS.getVariable("countA").incrementAndGet();
    }
});

var TriggerAPattern = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "a.*";
    },
    onRun: function(self, event) {
        self.logger.debug("Received matching event {}", event.name);
        EPS.getVariable("countAPattern").incrementAndGet();
    }
});

function onStartup() {
    ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ].forEach(function(name) {
        EPS.event(name).send();
    });
}
