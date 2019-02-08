/**
 * Sponge Knowledge base
 * Using rules - events
 */

var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.test.util.CorrelationEventsLog();
    sponge.setVariable("correlationEventsLog", correlationEventsLog);
}

// Naming F(irst), L(ast), A(ll), N(one)

var RuleFNF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :none", "e3"]).withCondition("e2", function(rule, event) {
            return Number(event.get("label")) > 4;
        });
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNF", self);
    }
});

var RuleFNNFReject = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :none", "e6 :none", "e3"]).withCondition("e2", this.e2LabelCondition);
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNFReject", self);
    },
    e2LabelCondition: function(self, event) {
        return 2 <= Number(event.get("label")) <= 4;
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
