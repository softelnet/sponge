/**
 * Sponge Knowledge base
 * Using knowledge base manager: enabling/disabling processors.
 * Note that auto-enable is turned off in the configuration.
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    // Variables for assertions only
    EPS.setVariable("verifyTriggerEnabled", new AtomicBoolean(false))
    EPS.setVariable("verifyTriggerDisabled", new AtomicBoolean(false))
    EPS.setVariable("verificationDone", new AtomicBoolean(false))
}

class VerifyTrigger extends Trigger {
    void configure() {
        this.eventName = "verify"
    }
    void run(Event event) {
        verifyManager()
    }
    void verifyManager() {
        int triggerCount = EPS.engine.triggers.size()
        EPS.enable(TriggerA)
        EPS.enable(TriggerA)
        EPS.getVariable("verifyTriggerEnabled").set(EPS.engine.triggers.size() == triggerCount + 1)
        triggerCount = EPS.engine.triggers.size()
        EPS.disable(TriggerA)
        EPS.disable(TriggerA)
        EPS.getVariable("verifyTriggerDisabled").set(EPS.engine.triggers.size() == triggerCount - 1)
        EPS.getVariable("verificationDone").set(true)
    }
}

class TriggerA extends Trigger {
    void configure() {
        this.eventName = "a"
    }
    void run(Event event) {
        //
    }
}

void onLoad() {
    EPS.enable(VerifyTrigger)
}

void onStartup() {
    EPS.event("verify").send()
}
