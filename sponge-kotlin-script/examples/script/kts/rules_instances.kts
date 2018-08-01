/*
 * Sponge Knowledge base
 * Rules - instances
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("countA", AtomicInteger(0))
    sponge.setVariable("countB", AtomicInteger(0))
    sponge.setVariable("max", 100)
}

class RuleA : Rule() {
    override fun onConfigure() {
        setEvents("a a1", "a a2")
    }

    override fun onRun(event: Event?) {
        sponge.getVariable<AtomicInteger>("countA").incrementAndGet()
    }
}

class RuleB : Rule() {
    override fun onConfigure() {
        setEvents("b b1", "b b2")
    }

    override fun onRun(event: Event?) {
        sponge.getVariable<AtomicInteger>("countB").incrementAndGet()
    }
}

fun onStartup() {
    for (i in 1..sponge.getVariable<Int>("max")) {
        sponge.event("a").send()
        sponge.event("b").send()
    }
}
