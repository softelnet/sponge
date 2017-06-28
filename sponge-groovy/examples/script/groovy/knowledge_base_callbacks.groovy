/**
 * Sponge Knowledge base
 * Using knowledge base callbacks.
 */

import java.util.concurrent.atomic.*
import org.openksavi.sponge.examples.TestStatus

class ReloadTrigger extends Trigger {
    void configure() {
        this.eventName = "reload"
    }
    void run(Event event) {
        this.logger.debug("Received event: {}", event.name)
        EPS.reload()
    }
}

void onInit() {
    // Variables for assertions only
    EPS.setVariable("onInitCalled", new AtomicBoolean(false))
    EPS.setVariable("onLoadCalled", new AtomicInteger(0))
    EPS.setVariable("onStartupCalled", new AtomicBoolean(false))
    EPS.setVariable("onBeforeReloadCalled", new AtomicBoolean(false))
    EPS.setVariable("onAfterReloadCalled", new AtomicBoolean(false))

    EPS.logger.debug("onInit")
    EPS.getVariable("onInitCalled").set(true)
}

void onLoad() {
    EPS.logger.debug("onLoad")
    EPS.getVariable("onLoadCalled").incrementAndGet()
}

void onStartup() {
    EPS.logger.debug("onStartup")
    EPS.getVariable("onStartupCalled").set(true)
    EPS.event("reload").sendAfter(1000)
}

void onShutdown() {
    EPS.logger.debug("onShutdown")
    // Using Java static field because all variables will be lost after shutdown .
    TestStatus.onShutdownCalled = true
}

void onBeforeReload() {
    EPS.logger.debug("onBeforeReload")
    EPS.getVariable("onBeforeReloadCalled").set(true)
}

void onAfterReload() {
    EPS.logger.debug("onAfterReload")
    EPS.getVariable("onAfterReloadCalled").set(true)
}
