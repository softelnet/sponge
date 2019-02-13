/*
 * Sponge Knowledge base
 * Triggers - Event pattern
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicInteger

class TriggersEventPattern : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("countA", AtomicInteger(0))
        sponge.setVariable("countAPattern", AtomicInteger(0))
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() {
            withEvent("a")
        }
        override fun onRun(event: Event) {
            sponge.getVariable<AtomicInteger>("countA").incrementAndGet()
        }
    }

    class TriggerAPattern : KTrigger() {
        override fun onConfigure() {
            withEvent("a.*")
        }
        override fun onRun(event: Event) {
            logger.debug("Received matching event {}", event.name)
            sponge.getVariable<AtomicInteger>("countAPattern").incrementAndGet()
        }
    }

    override fun onStartup() {
        for (name in listOf("a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1")) {
            sponge.event(name).send()
        }
    }
}
