/**
 * Sponge Knowledge base
 * Using knowledge base callbacks.
 */

import java.util.concurrent.atomic.*
import org.openksavi.sponge.examples.util.TestStatus

class ReloadTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("reload")
    }
    void onRun(Event event) {
        this.logger.debug("Received event: {}", event.name)
        sponge.reload()
    }
}

void onInit() {
    // Variables for assertions only
    sponge.setVariable("onInitCalled", new AtomicBoolean(false))
    sponge.setVariable("onBeforeLoadCalled", new AtomicInteger(0))
    sponge.setVariable("onLoadCalled", new AtomicInteger(0))
    sponge.setVariable("onAfterLoadCalled", new AtomicInteger(0))
    sponge.setVariable("onStartupCalled", new AtomicBoolean(false))
    sponge.setVariable("onBeforeReloadCalled", new AtomicBoolean(false))
    sponge.setVariable("onAfterReloadCalled", new AtomicBoolean(false))

    sponge.logger.debug("onInit")
    sponge.getVariable("onInitCalled").set(true)
}

void onBeforeLoad() {
    sponge.logger.debug("onBeforeLoad")
    sponge.getVariable("onBeforeLoadCalled").incrementAndGet()
}

void onLoad() {
    sponge.logger.debug("onLoad")
    sponge.getVariable("onLoadCalled").incrementAndGet()
}

void onAfterLoad() {
    sponge.logger.debug("onAfterLoad")
    sponge.getVariable("onAfterLoadCalled").incrementAndGet()
}

void onStartup() {
    sponge.logger.debug("onStartup")
    sponge.getVariable("onStartupCalled").set(true)
    sponge.event("reload").sendAfter(1000)
}

void onShutdown() {
    sponge.logger.debug("onShutdown")
    // Using Java static field because all variables will be lost after shutdown .
    TestStatus.onShutdownCalled = true
}

void onBeforeReload() {
    sponge.logger.debug("onBeforeReload")
    sponge.getVariable("onBeforeReloadCalled").set(true)
}

void onAfterReload() {
    sponge.logger.debug("onAfterReload")
    sponge.getVariable("onAfterReloadCalled").set(true)
}
