/**
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.core.util.CorrelationEventsLog();
    EPS.setVariable("correlationEventsLog", correlationEventsLog);
}

var RuleFFF = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e2", "e3 :first"];
        self.synchronous = true;
    },
    run: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFF", self);
    }
});

var RuleFFL = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e2", "e3 :last"];
        self.duration = Duration.ofMillis(500);
        self.synchronous = false;
    },
    run: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFL", self);
    }
});

function onStartup() {
    EPS.event("e1").set("label", "1").sendAfter(1);
    EPS.event("e2").set("label", "2").sendAfter(2);
    EPS.event("e2").set("label", "3").sendAfter(3);
    EPS.event("e2").set("label", "4").sendAfter(4);
    EPS.event("e3").set("label", "5").sendAfter(5);
    EPS.event("e3").set("label", "6").sendAfter(6);
    EPS.event("e3").set("label", "7").sendAfter(7)
};
