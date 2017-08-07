/**
 * Sponge Knowledge base
 * Using knowledge base manager: enabling/disabling processors.
 * Note that auto-enable is turned off in the configuration.
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("verifyTriggerEnabled", new AtomicBoolean(false));
    EPS.setVariable("verifyTriggerDisabled", new AtomicBoolean(false));
    EPS.setVariable("verificationDone", new AtomicBoolean(false));
}

function verifyManager() {
    var triggerCount = EPS.engine.triggers.size();
    EPS.enable(TriggerA);
    EPS.enable(TriggerA);
    EPS.getVariable("verifyTriggerEnabled").set(EPS.engine.triggers.size() == triggerCount + 1);
    triggerCount = EPS.engine.triggers.size();
    EPS.disable(TriggerA);
    EPS.disable(TriggerA);
    EPS.getVariable("verifyTriggerDisabled").set(EPS.engine.triggers.size() == triggerCount - 1);
    EPS.getVariable("verificationDone").set(true);
}

var VerifyTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "verify";
    },
    onRun: function(self, event) {
        verifyManager();
    }
});

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "a";
    },
    onRun: function(self, event) {
        //
    }
});

function onLoad() {
    EPS.enable(VerifyTrigger);
}

function onStartup() {
    EPS.event("verify").send();
}
