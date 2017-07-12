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

var RuleFNF = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e2 :none", "e3"];
        self.setConditions("e2", function(self, event) {
            return Number(event.get("label")) > 4;
        });
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNF", self);
    }
});

var RuleFNNFReject = Java.extend(Rule, {
    configure: function(self) {
        self.events = ["e1", "e2 :none", "e6 :none", "e3"];
        self.setConditions("e2", this.e2LabelCondition);
    },
    run: function(self, event) {
        self.logger.debug("Running rule for events: {}", self.eventAliasMap);
        correlationEventsLog.addEvents("RuleFNNFReject", self);
    },
    e2LabelCondition: function(self, event) {
        return 2 <= Number(event.get("label")) <= 4;
    }
});

function onStartup() {
    EPS.event("e1").set("label", "1").send();
    EPS.event("e2").set("label", "2").send();
    EPS.event("e2").set("label", "3").send();
    EPS.event("e2").set("label", "4").send();
    EPS.event("e3").set("label", "5").send();
    EPS.event("e3").set("label", "6").send();
    EPS.event("e3").set("label", "7").send();
}
