/*
 * Sponge Knowledge base
 * Trigger builders
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.kotlin.KTriggerBuilder

class TriggersBuilder : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("receivedEventA", AtomicBoolean(false))
        sponge.setVariable("receivedEventBCount", AtomicInteger(0))
    }

    override fun onLoad() {
        sponge.enable(KTriggerBuilder("TriggerA").withEvent("a").withOnRun({ trigger, event ->
            trigger.logger.debug("Received event {}", event)
            sponge.getVariable<AtomicBoolean>("receivedEventA").set(true)
        }))

        sponge.enable(KTriggerBuilder("TriggerB").withEvent("b").withOnRun({ trigger, event ->
            trigger.logger.debug("Received event {}", event)
            val receivedEventBCount = sponge.getVariable<AtomicInteger>("receivedEventBCount")
            if (receivedEventBCount.get() == 0) {
                trigger.logger.debug("Statistics: {}", sponge.statisticsSummary)
            }
            receivedEventBCount.incrementAndGet()
        }))
    }

    override fun onStartup() {
        logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
        logger.debug("Knowledge base name: {}", sponge.kb.name)
        sponge.event("a").send()
        sponge.event("b").sendAfter(200, 200)
    }
}
