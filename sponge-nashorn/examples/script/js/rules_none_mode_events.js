/**
 * Sponge Knowledge base
 * Using rules - events
 */

var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.test.util.CorrelationEventsLog();
    EPS.setVariable("correlationEventsLog", correlationEventsLog);
}

// Naming F(irst), L(ast), A(ll), N(one)

var RuleFNNF = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e5 :none", "e6 :none", "e3"];
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNF", self);
    }
});

var RuleFNNNL = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"];
        self.duration = Duration.ofSeconds(2);
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNNL", self);
    }
});

var RuleFNNNLReject = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"];
        self.duration = Duration.ofSeconds(2);
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNNLRejected", self);
    }
});

var RuleFNFNL = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e5 :none", "e2", "e7 :none", "e3 :last"];
        self.duration = Duration.ofSeconds(2);
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNFNL", self);
    }
});

function onStartup() {
    EPS.event("e1").set("label", "1").sendAfter(100);
    EPS.event("e2").set("label", "2").sendAfter(200);
    EPS.event("e2").set("label", "3").sendAfter(300);
    EPS.event("e2").set("label", "4").sendAfter(400);
    EPS.event("e3").set("label", "5").sendAfter(500);
    EPS.event("e3").set("label", "6").sendAfter(600);
    EPS.event("e3").set("label", "7").sendAfter(700);
}
