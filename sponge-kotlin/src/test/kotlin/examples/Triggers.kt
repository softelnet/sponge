/*
 * Sponge Knowledge Base
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
        sponge.setVariable("receivedEventA", AtomicBoolean(false))
        sponge.setVariable("receivedEventBCount", AtomicInteger(0))
        sponge.setVariable("receivedEventTestJavaCount", AtomicInteger(0))
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() {
            withEvent("a")
        }
        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            sponge.getVariable<AtomicBoolean>("receivedEventA").set(true)
        }
    }

    class TriggerB : KTrigger() {
        override fun onConfigure() {
            withEvent("b")
        }
        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            val receivedEventBCount = sponge.getVariable<AtomicInteger>("receivedEventBCount")
            if (receivedEventBCount.get() == 0) {
                logger.debug("Statistics: {}", sponge.statisticsSummary)
            }
            receivedEventBCount.incrementAndGet()
        }
    }

    override fun onLoad() = sponge.enableJava(SampleJavaTrigger::class.java)

    override fun onStartup() {
        logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
        logger.debug("Knowledge base name: {}", sponge.kb.name)
        sponge.event("a").send()
        sponge.event("b").sendAfter(200, 200)
        sponge.event("testJava").send()
    }

    override fun onShutdown() {
        logger.debug("Shutting down")
    }
}
