/**
 * Sponge Knowledge base Using knowledge base callbacks.
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var TestStatus = org.openksavi.sponge.test.util.TestStatus;

var ReloadTrigger = Java.extend(Trigger, {
    onConfigure : function(self) {
        self.withEvent("reload");
    },
    onRun : function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        // sponge.requestReload();
        sponge.reload();
    }
});

function onInit() {
    // Variables for assertions only
    sponge.setVariable("onInitCalled", new AtomicBoolean(false));
    sponge.setVariable("onBeforeLoadCalled", new AtomicInteger(0));
    sponge.setVariable("onLoadCalled", new AtomicInteger(0));
    sponge.setVariable("onAfterLoadCalled", new AtomicInteger(0));
    sponge.setVariable("onStartupCalled", new AtomicBoolean(false));
    sponge.setVariable("onBeforeReloadCalled", new AtomicBoolean(false));
    sponge.setVariable("onAfterReloadCalled", new AtomicBoolean(false));

    sponge.logger.debug("onInit");
    sponge.getVariable("onInitCalled").set(true);
}

function onBeforeLoad() {
    sponge.logger.debug("onBeforeLoad");
    sponge.getVariable("onBeforeLoadCalled").incrementAndGet();
}

function onLoad() {
    sponge.logger.debug("onLoad");
    sponge.getVariable("onLoadCalled").incrementAndGet();
}

function onAfterLoad() {
    sponge.logger.debug("onAfterLoad");
    sponge.getVariable("onAfterLoadCalled").incrementAndGet();
}

function onStartup() {
    sponge.logger.debug("onStartup");
    sponge.getVariable("onStartupCalled").set(true);
    sponge.event("reload").sendAfter(1000);
}

function onShutdown() {
    sponge.logger.debug("onShutdown");
    // Using Java static field because all variables will be lost after shutdown
    TestStatus.onShutdownCalled = true;
}

function onBeforeReload() {
    sponge.logger.debug("onBeforeReload");
    sponge.getVariable("onBeforeReloadCalled").set(true);
}

function onAfterReload() {
    sponge.logger.debug("onAfterReload");
    sponge.getVariable("onAfterReloadCalled").set(true);
}
