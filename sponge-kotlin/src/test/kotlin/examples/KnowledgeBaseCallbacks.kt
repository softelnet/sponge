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
        override fun onConfigure() {
            withEvent("reload")
        }
        override fun onRun(event: Event) {
            logger.debug("Received event: {}", event.name)
            sponge.reload()
        }
    }

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("onInitCalled", AtomicBoolean(false))
        sponge.setVariable("onBeforeLoadCalled", AtomicInteger(0))
        sponge.setVariable("onLoadCalled", AtomicInteger(0))
        sponge.setVariable("onAfterLoadCalled", AtomicInteger(0))
        sponge.setVariable("onStartupCalled", AtomicBoolean(false))
        sponge.setVariable("onBeforeReloadCalled", AtomicBoolean(false))
        sponge.setVariable("onAfterReloadCalled", AtomicBoolean(false))

        logger.debug("onInit")
        sponge.getVariable<AtomicBoolean>("onInitCalled").set(true)
    }

    override fun onBeforeLoad() {
        logger.debug("onBeforeLoad")
        sponge.getVariable<AtomicInteger>("onBeforeLoadCalled").incrementAndGet()
    }

    override fun onLoad() {
        logger.debug("onLoad")
        sponge.getVariable<AtomicInteger>("onLoadCalled").incrementAndGet()
    }

    override fun onAfterLoad() {
        logger.debug("onAfterLoad")
        sponge.getVariable<AtomicInteger>("onAfterLoadCalled").incrementAndGet()
    }

    override fun onStartup() {
        logger.debug("onStartup")
        sponge.getVariable<AtomicBoolean>("onStartupCalled").set(true)
        sponge.event("reload").sendAfter(1000)
    }

    override fun onShutdown() {
        logger.debug("onShutdown")
        // Using Java static field because all variables will be lost after shutdown.
        TestStatus.onShutdownCalled = true
    }

    override fun onBeforeReload() {
        logger.debug("onBeforeReload")
        sponge.getVariable<AtomicBoolean>("onBeforeReloadCalled").set(true)
    }

    override fun onAfterReload() {
        logger.debug("onAfterReload")
        sponge.getVariable<AtomicBoolean>("onAfterReloadCalled").set(true)
    }
}
