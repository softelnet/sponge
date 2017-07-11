/**
 * Sponge Knowledge base Using knowledge base callbacks.
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;
var TestStatus = org.openksavi.sponge.test.TestStatus;

var ReloadTrigger = Java.extend(Trigger, {
    configure : function(self) {
        self.event = "reload";
    },
    run : function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        // EPS.requestReload();
        EPS.reload();
    }
});

function onInit() {
    // Variables for assertions only
    EPS.setVariable("onInitCalled", new AtomicBoolean(false));
    EPS.setVariable("onLoadCalled", new AtomicInteger(0));
    EPS.setVariable("onStartupCalled", new AtomicBoolean(false));
    EPS.setVariable("onBeforeReloadCalled", new AtomicBoolean(false));
    EPS.setVariable("onAfterReloadCalled", new AtomicBoolean(false));

    EPS.logger.debug("onInit");
    EPS.getVariable("onInitCalled").set(true);
}

function onLoad() {
    EPS.logger.debug("onLoad");
    EPS.getVariable("onLoadCalled").incrementAndGet();
}

function onStartup() {
    EPS.logger.debug("onStartup");
    EPS.getVariable("onStartupCalled").set(true);
    EPS.event("reload").sendAfter(1000);
}

function onShutdown() {
    EPS.logger.debug("onShutdown");
    // Using Java static field because all variables will be lost after shutdown
    TestStatus.onShutdownCalled = true;
}

function onBeforeReload() {
    EPS.logger.debug("onBeforeReload");
    EPS.getVariable("onBeforeReloadCalled").set(true);
}

function onAfterReload() {
    EPS.logger.debug("onAfterReload");
    EPS.getVariable("onAfterReloadCalled").set(true);
}
