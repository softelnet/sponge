/*
 * Sponge Knowledge Base
 * Using knowledge base manager: enabling/disabling processors.
 * Note that auto-enable is turned off in the configuration.
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.EngineOperations
import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicBoolean

class KnowledgeBaseManager : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("verifyTriggerEnabled", AtomicBoolean(false))
        sponge.setVariable("verifyTriggerDisabled", AtomicBoolean(false))
        sponge.setVariable("verificationDone", AtomicBoolean(false))
    }

    class VerifyTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("verify")
        }

        override fun onRun(event: Event) = verifyManager()

        fun verifyManager() {
            var triggerCount = sponge.engine.triggers.size
            sponge.enable(TriggerA::class)
            sponge.enable(TriggerA::class)
            sponge.getVariable<AtomicBoolean>("verifyTriggerEnabled").set(sponge.engine.triggers.size == triggerCount + 1)
            triggerCount = sponge.engine.triggers.size
            sponge.disable(TriggerA::class)
            sponge.disable(TriggerA::class)
            sponge.getVariable<AtomicBoolean>("verifyTriggerDisabled").set(sponge.engine.triggers.size == triggerCount - 1)
            sponge.getVariable<AtomicBoolean>("verificationDone").set(true)
        }
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() {
            withEvent("a")
        }
        override fun onRun(event: Event) {
        }
    }

    override fun onLoad() {
        sponge.enable(VerifyTrigger::class)
    }

    override fun onStartup() {
        sponge.event("verify").send()
    }
}
