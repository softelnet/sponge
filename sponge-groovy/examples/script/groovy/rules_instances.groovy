/**
 * Sponge Knowledge base
 * Rules - instances
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    sponge.setVariable("countA", new AtomicInteger(0))
    sponge.setVariable("countB", new AtomicInteger(0))
    sponge.setVariable("max", 100)
}

class RuleA extends Rule {
    void onConfigure() {
        this.events = ["a a1", "a a2"]
    }
    void onRun(Event event) {
        sponge.getVariable("countA").incrementAndGet()
    }
}

class RuleB extends Rule {
    void onConfigure() {
        this.events = ["b b1", "b b2"]
    }
    void onRun(Event event) {
        sponge.getVariable("countB").incrementAndGet()
    }
}

void onStartup() {
    for (i in (0..<sponge.getVariable("max"))) {
        sponge.event("a").send()
        sponge.event("b").send()
    }
}
