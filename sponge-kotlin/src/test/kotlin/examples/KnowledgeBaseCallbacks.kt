/*
 * Sponge Knowledge base
 * Using knowledge base callbacks.
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import org.openksavi.sponge.test.util.TestStatus
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class KnowledgeBaseCallbacks : KKnowledgeBase() {

    class ReloadTrigger : KTrigger() {
        override fun onConfigure() = setEvent("reload")
        override fun onRun(event: Event) {
            logger.debug("Received event: {}", event.name)
            eps.reload()
        }
    }

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("onInitCalled", AtomicBoolean(false))
        eps.setVariable("onLoadCalled", AtomicInteger(0))
        eps.setVariable("onStartupCalled", AtomicBoolean(false))
        eps.setVariable("onBeforeReloadCalled", AtomicBoolean(false))
        eps.setVariable("onAfterReloadCalled", AtomicBoolean(false))

        logger.debug("onInit")
        eps.getVariable<AtomicBoolean>("onInitCalled").set(true)
    }

    override fun onLoad() {
        logger.debug("onLoad")
        eps.getVariable<AtomicInteger>("onLoadCalled").incrementAndGet()
    }

    override fun onStartup() {
        logger.debug("onStartup")
        eps.getVariable<AtomicBoolean>("onStartupCalled").set(true)
        eps.event("reload").sendAfter(1000)
    }

    override fun onShutdown() {
        logger.debug("onShutdown")
        // Using Java static field because all variables will be lost after shutdown.
        TestStatus.onShutdownCalled = true
    }

    override fun onBeforeReload() {
        logger.debug("onBeforeReload")
        eps.getVariable<AtomicBoolean>("onBeforeReloadCalled").set(true)
    }

    override fun onAfterReload() {
        logger.debug("onAfterReload")
        eps.getVariable<AtomicBoolean>("onAfterReloadCalled").set(true)
    }
}
