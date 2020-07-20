/**
 * Sponge Knowledge Base
 * Script - Overriding
 * Note that auto-enable is turned off in the configuration.
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    //Variables for assertions only
    sponge.setVariable("receivedEventA1", new AtomicInteger(0));
    sponge.setVariable("receivedEventA2", new AtomicInteger(0));
    sponge.setVariable("functionA1", new AtomicInteger(0));
    sponge.setVariable("functionA2", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a");
    },
    onRun: function(self, event) {
        sponge.getVariable("receivedEventA1").set(1);
    }
});

// Execute immediately while loading
sponge.enable(TriggerA);

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a");
    },
    onRun: function(self, event) {
        sponge.getVariable("receivedEventA2").set(2);
    }
});

// Execute immediately while loading
sponge.enable(TriggerA);

function onStartup() {
    sponge.event("a").send();
    functionA();
}

function functionA() {
    sponge.getVariable("functionA1").set(1);
}

function functionA() {
    sponge.getVariable("functionA2").set(2);
}

