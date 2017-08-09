/**
 * Sponge Knowledge base
 * Auto-enable
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("counter", new AtomicInteger(0));
}

var AutoAction = Java.extend(Action, {
    onCall: function(self, args) {
        self.logger.debug("Running");
        EPS.getVariable("counter").incrementAndGet();
        return args;
    }
});

var AutoFilter = Java.extend(Filter, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onAccept: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        EPS.getVariable("counter").incrementAndGet();
        return true;
    }
});

var AutoTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "e1";
    },
    onRun: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        EPS.getVariable("counter").incrementAndGet();
    }
});

var AutoRule = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["e1", "e2"];
    },
    onRun: function(self, event) {
        self.logger.debug("Running for sequence: {}", self.eventSequence);
        EPS.getVariable("counter").incrementAndGet();
    }
});

var AutoCorrelator = Java.extend(Correlator, {
    onConfigure: function(self) {
        self.events = ["e1", "e2"];
    },
    onAcceptAsFirst: function(self, event) {
        return event.name == "e1";
    },
    onEvent: function(self, event) {
        self.logger.debug("Received event: {}", event.name);
        if (event.name == "e2") {
                EPS.getVariable("counter").incrementAndGet();
                self.finish();
        }
    }
});

function onStartup() {
    EPS.callAction("AutoAction");
    EPS.event("e1").send();
    EPS.event("e2").send();
}
