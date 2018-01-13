/*
 * Sponge Knowledge base
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
        eps.setVariable("verifyTriggerEnabled", AtomicBoolean(false))
        eps.setVariable("verifyTriggerDisabled", AtomicBoolean(false))
        eps.setVariable("verificationDone", AtomicBoolean(false))
    }

    class VerifyTrigger : KTrigger() {
        override fun onConfigure() = setEvent("verify")

        override fun onRun(event: Event) = verifyManager()

        fun verifyManager() {
            var triggerCount = eps.engine.triggers.size
            eps.enable(TriggerA::class)
            eps.enable(TriggerA::class)
            eps.getVariable<AtomicBoolean>("verifyTriggerEnabled").set(eps.engine.triggers.size == triggerCount + 1)
            triggerCount = eps.engine.triggers.size
            eps.disable(TriggerA::class)
            eps.disable(TriggerA::class)
            eps.getVariable<AtomicBoolean>("verifyTriggerDisabled").set(eps.engine.triggers.size == triggerCount - 1)
            eps.getVariable<AtomicBoolean>("verificationDone").set(true)
        }
    }

    class TriggerA : KTrigger() {
        override fun onConfigure() = setEvent("a")
        override fun onRun(event: Event) {
        }
    }

    override fun onLoad() {
        eps.enable(VerifyTrigger::class)
    }

    override fun onStartup() {
        eps.event("verify").send()
    }
}
