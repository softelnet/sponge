/*
 * Sponge Knowledge base
 * Rules - instances
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("countA", AtomicInteger(0))
    EPS.setVariable("countB", AtomicInteger(0))
    EPS.setVariable("max", 100)
}

class RuleA : Rule() {
    override fun onConfigure() {
        setEvents("a a1", "a a2")
    }

    override fun onRun(event: Event?) {
        eps.getVariable<AtomicInteger>("countA").incrementAndGet()
    }
}

class RuleB : Rule() {
    override fun onConfigure() {
        setEvents("b b1", "b b2")
    }

    override fun onRun(event: Event?) {
        eps.getVariable<AtomicInteger>("countB").incrementAndGet()
    }
}

fun onStartup() {
    for (i in 1..EPS.getVariable<Int>("max")) {
        EPS.event("a").send()
        EPS.event("b").send()
    }
}
