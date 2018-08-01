/**
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.test.util.CorrelationEventsLog();
    sponge.setVariable("correlationEventsLog", correlationEventsLog);
}

var RuleFFF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["e1", "e2", "e3 :first"];
        self.synchronous = true;
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFF", self);
    }
});

var RuleFFL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["e1", "e2", "e3 :last"];
        self.duration = Duration.ofSeconds(2);
        self.synchronous = false;
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFL", self);
    }
});

function onStartup() {
    sponge.event("e1").set("label", "1").send();
    sponge.event("e2").set("label", "2").send();
    sponge.event("e2").set("label", "3").send();
    sponge.event("e2").set("label", "4").send();
    sponge.event("e3").set("label", "5").send();
    sponge.event("e3").set("label", "6").send();
    sponge.event("e3").set("label", "7").send();
}
