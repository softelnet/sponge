/*
 * Sponge Knowledge base
 * Rules - instances
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import java.util.concurrent.atomic.AtomicInteger

class RulesInstances : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("countA", AtomicInteger(0))
        sponge.setVariable("countB", AtomicInteger(0))
        sponge.setVariable("max", 100)
    }

    class RuleA : KRule() {
        override fun onConfigure() = setEvents("a a1", "a a2")

        override fun onRun(event: Event?) {
            sponge.getVariable<AtomicInteger>("countA").incrementAndGet()
        }
    }

    class RuleB : KRule() {
        override fun onConfigure() = setEvents("b b1", "b b2")

        override fun onRun(event: Event?) {
            sponge.getVariable<AtomicInteger>("countB").incrementAndGet()
        }
    }

    override fun onStartup() {
        for (i in 1..sponge.getVariable<Int>("max")) {
            sponge.event("a").send()
            sponge.event("b").send()
        }
    }
}
