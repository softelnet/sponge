/**
 * Sponge Knowledge base
 * Triggers - Generating events and using triggers
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean;
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("receivedEventA", new AtomicBoolean(false));
    EPS.setVariable("receivedEventBCount", new AtomicInteger(0));
    EPS.setVariable("receivedEventTestJavaCount", new AtomicInteger(0));
}

var TriggerA = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "a";
    },
    onRun: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        EPS.getVariable("receivedEventA").set(true);
    }
});

var TriggerB = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "b";
    },
    onRun: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        if (EPS.getVariable("receivedEventBCount").get() == 0) {
            self.logger.debug("Statistics: {}", EPS.statisticsSummary);
        }
        EPS.getVariable("receivedEventBCount").incrementAndGet();
    }
});

function onLoad() {
    EPS.enableJava(org.openksavi.sponge.examples.SampleJavaTrigger.class);
}

function onStartup() {
    EPS.logger.debug("Startup {}, triggers: {}", EPS.info, EPS.engine.triggers);
    EPS.logger.debug("Knowledge base name: {}", EPS.kb.name);
    EPS.event("a").send();
    EPS.event("b").sendAfter(200, 200);
    EPS.event("testJava").send();
}

function onShutdown() {
    EPS.logger.debug("Shutting down");
}
