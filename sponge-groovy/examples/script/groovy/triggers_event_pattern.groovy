/**
 * Sponge Knowledge base
 * Triggers - Event pattern
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    EPS.setVariable("countA", new AtomicInteger(0))
    EPS.setVariable("countAPattern", new AtomicInteger(0))
}

class TriggerA extends Trigger {
    void onConfigure() {
        this.event = "a"
    }
    void onRun(Event event) {
        EPS.getVariable("countA").incrementAndGet()
    }
}

class TriggerAPattern extends Trigger {
    void onConfigure() {
        this.event = "a.*"
    }
    void onRun(Event event) {
        this.logger.debug("Received matching event {}", event.name)
        EPS.getVariable("countAPattern").incrementAndGet()
    }
}

void onStartup() {
    for (name in ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ]) {
        EPS.event(name).send()
    }
}
