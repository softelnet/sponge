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
        eps.setVariable("countA", AtomicInteger(0))
        eps.setVariable("countAPattern", AtomicInteger(0))
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() = setEvent("a")
        override fun onRun(event: Event) {
            eps.getVariable<AtomicInteger>("countA").incrementAndGet()
        }
    }

    class TriggerAPattern : KTrigger() {
        override fun onConfigure() = setEvent("a.*")
        override fun onRun(event: Event) {
            logger.debug("Received matching event {}", event.name)
            eps.getVariable<AtomicInteger>("countAPattern").incrementAndGet()
        }
    }

    override fun onStartup() {
        for (name in listOf("a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1")) {
            eps.event(name).send()
        }
    }
}
