/*
 * Sponge Knowledge Base
 * Auto-enable
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KCorrelator
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicInteger

class KnowledgeBaseAutoEnable : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("counter", AtomicInteger(0))
    }

    class AutoAction : KAction() {
        fun onCall(): Any? {
            logger.debug("Running")
            sponge.getVariable<AtomicInteger>("counter").incrementAndGet()
            return null
        }
    }

    class AutoFilter : KFilter() {
        override fun onConfigure() {
            withEvent("e1")
        }
        override fun onAccept(event: Event): Boolean {
            logger.debug("Received event: {}", event.name)
            sponge.getVariable<AtomicInteger>("counter").incrementAndGet()
            return true
        }
    }

    class AutoTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("e1")
        }
        override fun onRun(event: Event) {
            logger.debug("Received event: {}", event.name)
            sponge.getVariable<AtomicInteger>("counter").incrementAndGet()
        }
    }

    class AutoRule : KRule() {
        override fun onConfigure() {
            withEvents("e1", "e2")
        }
        override fun onRun(event: Event) {
            logger.debug("Running for sequence: {}", eventSequence)
            sponge.getVariable<AtomicInteger>("counter").incrementAndGet()
        }
    }

    class AutoCorrelator : KCorrelator() {
        override fun onConfigure() {
            withEvents("e1", "e2")
        }

        override fun onAcceptAsFirst(event: Event) = event.name == "e1"

        override fun onEvent(event: Event) {
            logger.debug("Received event: {}", event.name)
            if (event.name == "e2") {
                sponge.getVariable<AtomicInteger>("counter").incrementAndGet()
                finish()
            }
        }
    }

    override fun onStartup() {
        sponge.call("AutoAction")
        sponge.event("e1").send()
        sponge.event("e2").send()
    }
}
