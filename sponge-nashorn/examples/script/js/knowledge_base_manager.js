/**
 * Sponge Knowledge Base
 * Using knowledge base manager: enabling/disabling processors.
 * Note that auto-enable is turned off in the configuration.
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("verifyTriggerEnabled", new AtomicBoolean(false));
    sponge.setVariable("verifyTriggerDisabled", new AtomicBoolean(false));
    sponge.setVariable("verificationDone", new AtomicBoolean(false));
}

function verifyManager() {
    var triggerCount = sponge.engine.triggers.size();
    sponge.enable(TriggerA);
    sponge.enable(TriggerA);
    sponge.getVariable("verifyTriggerEnabled").set(sponge.engine.triggers.size() == triggerCount + 1);
    triggerCount = sponge.engine.triggers.size();
    sponge.disable(TriggerA);
    sponge.disable(TriggerA);
    sponge.getVariable("verifyTriggerDisabled").set(sponge.engine.triggers.size() == triggerCount - 1);
    sponge.getVariable("verificationDone").set(true);
}

var VerifyTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("verify");
    },
    onRun: function(self, event) {
        verifyManager();
    }
});

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a");
    },
    onRun: function(self, event) {
        //
    }
});

function onLoad() {
    sponge.enable(VerifyTrigger);
}

function onStartup() {
    sponge.event("verify").send();
}
