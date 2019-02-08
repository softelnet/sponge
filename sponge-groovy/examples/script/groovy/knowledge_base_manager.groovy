/**
 * Sponge Knowledge base
 * Using knowledge base manager: enabling/disabling processors.
 * Note that auto-enable is turned off in the configuration.
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    // Variables for assertions only
    sponge.setVariable("verifyTriggerEnabled", new AtomicBoolean(false))
    sponge.setVariable("verifyTriggerDisabled", new AtomicBoolean(false))
    sponge.setVariable("verificationDone", new AtomicBoolean(false))
}

class VerifyTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("verify")
    }
    void onRun(Event event) {
        verifyManager()
    }
    void verifyManager() {
        int triggerCount = sponge.engine.triggers.size()
        sponge.enable(TriggerA)
        sponge.enable(TriggerA)
        sponge.getVariable("verifyTriggerEnabled").set(sponge.engine.triggers.size() == triggerCount + 1)
        triggerCount = sponge.engine.triggers.size()
        sponge.disable(TriggerA)
        sponge.disable(TriggerA)
        sponge.getVariable("verifyTriggerDisabled").set(sponge.engine.triggers.size() == triggerCount - 1)
        sponge.getVariable("verificationDone").set(true)
    }
}

class TriggerA extends Trigger {
    void onConfigure() {
        this.withEvent("a")
    }
    void onRun(Event event) {
        //
    }
}

void onLoad() {
    sponge.enable(VerifyTrigger)
}

void onStartup() {
    sponge.event("verify").send()
}
