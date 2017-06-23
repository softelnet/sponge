/**
 * Sponge Knowledge base
 * Script - Overriding
 * Note that auto-enable is turned off in the configuration.
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    //Variables for assertions only
    EPS.setVariable("receivedEventA1", new AtomicInteger(0));
    EPS.setVariable("receivedEventA2", new AtomicInteger(0));
    EPS.setVariable("functionA1", new AtomicInteger(0));
    EPS.setVariable("functionA2", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    configure: function(self) {
        self.eventName = "a";
    },
    run: function(self, event) {
        EPS.getVariable("receivedEventA1").set(1);
    }
});

// Execute immediately while loading
EPS.enable(TriggerA);

var TriggerA = Java.extend(Trigger, {
    configure: function(self) {
        self.eventName = "a";
    },
    run: function(self, event) {
        EPS.getVariable("receivedEventA2").set(2);
    }
});

// Execute immediately while loading
EPS.enable(TriggerA);

function onStartup() {
    EPS.event("a").send();
    functionA();
}

function functionA() {
    EPS.getVariable("functionA1").set(1);
}

function functionA() {
    EPS.getVariable("functionA2").set(2);
}

