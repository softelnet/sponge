/**
 * Sponge Knowledge base
 * Using rules - events
 */

var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.examples.util.CorrelationEventsLog();
    sponge.setVariable("correlationEventsLog", correlationEventsLog);
}

// Naming F(irst), L(ast), A(ll), N(one)

var RuleFNNF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e6 :none", "e3"]);
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNF", self);
    }
});

var RuleFNNNL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNNL", self);
    }
});

var RuleFNNNLReject = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNNLRejected", self);
    }
});

var RuleFNFNL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNFNL", self);
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
