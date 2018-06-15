/*
 * Sponge Knowledge base
 * Triggers - Generating events and using triggers
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.SampleJavaTrigger
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Triggers : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("receivedEventA", AtomicBoolean(false))
        eps.setVariable("receivedEventBCount", AtomicInteger(0))
        eps.setVariable("receivedEventTestJavaCount", AtomicInteger(0))
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() = setEvent("a")
        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            eps.getVariable<AtomicBoolean>("receivedEventA").set(true)
        }
    }

    class TriggerB : KTrigger() {
        override fun onConfigure() = setEvent("b")
        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            val receivedEventBCount = eps.getVariable<AtomicInteger>("receivedEventBCount")
            if (receivedEventBCount.get() == 0) {
                logger.debug("Statistics: {}", eps.statisticsSummary)
            }
            receivedEventBCount.incrementAndGet()
        }
    }

    override fun onLoad() = eps.enableJava(SampleJavaTrigger::class.java)

    override fun onStartup() {
        logger.debug("Startup {}, triggers: {}", eps.info, eps.engine.triggers)
        logger.debug("Knowledge base name: {}", eps.kb.name)
        eps.event("a").send()
        eps.event("b").sendAfter(200, 200)
        eps.event("testJava").send()
    }

    override fun onShutdown() {
        logger.debug("Shutting down")
    }
}
