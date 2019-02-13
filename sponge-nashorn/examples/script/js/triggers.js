/**
 * Sponge Knowledge base
 * Triggers - Generating events and using triggers
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("receivedEventA", new AtomicBoolean(false));
    sponge.setVariable("receivedEventBCount", new AtomicInteger(0));
    sponge.setVariable("receivedEventTestJavaCount", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a");
    },
    onRun: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        sponge.getVariable("receivedEventA").set(true);
    }
});

var TriggerB = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("b");
    },
    onRun: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        if (sponge.getVariable("receivedEventBCount").get() == 0) {
            self.logger.debug("Statistics: {}", sponge.statisticsSummary);
        }
        sponge.getVariable("receivedEventBCount").incrementAndGet();
    }
});

function onLoad() {
    sponge.enableJava(org.openksavi.sponge.examples.SampleJavaTrigger.class);
}

function onStartup() {
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers);
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name);
    sponge.event("a").send();
    sponge.event("b").sendAfter(200, 200);
    sponge.event("testJava").send();
}

function onShutdown() {
    sponge.logger.debug("Shutting down");
}
