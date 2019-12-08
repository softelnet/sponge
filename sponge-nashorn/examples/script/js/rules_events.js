/**
 * Sponge Knowledge base
 * Using rules - events
 */

var defaultDuration = 1000;
var correlationEventsLog;

function onInit() {
    // Variables for assertions only
    correlationEventsLog = new org.openksavi.sponge.examples.util.CorrelationEventsLog();
    sponge.setVariable("correlationEventsLog", correlationEventsLog);
}

// Naming F(irst), L(ast), A(ll), N(one)
var RuleF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1"]);
    },
    onRun: function(self, event) {
        correlationEventsLog.addEvents("RuleF", self)
    }
});

// F(irst)F(irst)F(irst)
var RuleFFF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2", "e3 :first"]);
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFF", self);
    }
});

var RuleFFFDuration = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2", "e3 :first"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFFDuration", self);
    }
});

// F(irst)F(irst)L(ast)
var RuleFFL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2", "e3 :last"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}", event.name);
        correlationEventsLog.addEvents("RuleFFL", self);
    }
});

// F(irst)F(irst)A(ll)
var RuleFFA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2", "e3 :all"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFFA", self);
    }
});

// F(irst)F(irst)N(one)
var RuleFFN = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2", "e4 :none"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFFN", self);
    }
});

// F(irst)L(ast)F(irst)
var RuleFLF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :last", "e3 :first"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFLF", self);
    }
});

// F(irst)L(ast)L(ast)
var RuleFLL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :last", "e3 :last"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFLL", self);
    }
});

// F(irst)L(ast)A(ll)
var RuleFLA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :last", "e3 :all"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFLA", self);
    }
});

// F(irst)L(ast)N(one)
var RuleFLN = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :last", "e4 :none"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFLN", self);
    }
});

// F(irst)A(ll)F(irst)
var RuleFAF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :all", "e3 :first"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFAF", self);
    }
});

// F(irst)A(ll)L(ast)
var RuleFAL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :all", "e3 :last"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFAL", self);
    }
});

// F(irst)A(ll)A(ll)
var RuleFAA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :all", "e3 :all"]).withDuration( Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for event: {}, sequence: {}", event.name, self.eventSequence);
        correlationEventsLog.addEvents("RuleFAA", self);
    }
});

// F(irst)A(ll)N(one)
var RuleFAN = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :all", "e5 :none"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFAN", self);
    }
});

// F(irst)N(one)F(irst)
var RuleFNF = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e3"]);
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFNF", self);
    }
});

// F(irst)N(one)L(ast)
var RuleFNL = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e3 :last"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFNL", self);
    }
});

// F(irst)N(one)A(ll)
var RuleFNA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e5 :none", "e3 :all"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFNA", self);
    }
});

var RuleFNFReject = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["e1", "e2 :none", "e3"]).withDuration(Duration.ofMillis(defaultDuration));
    },
    onRun: function(self, event) {
        self.logger.debug("Running rule for sequence: {}", self.eventSequence);
        correlationEventsLog.addEvents("RuleFNFReject", self);
    }
});

function onStartup() {
    sponge.event("e1").set("label", "0").sendAfter(0, 200);  // Not used in assertions, "background noise" events.
    sponge.event("e1").set("label", "-1").sendAfter(0, 200);
    sponge.event("e1").set("label", "-2").sendAfter(0, 200);
    sponge.event("e1").set("label", "-3").sendAfter(0, 200);

    sponge.event("e1").set("label", "1").send();
    sponge.event("e2").set("label", "2").send();
    sponge.event("e2").set("label", "3").send();
    sponge.event("e2").set("label", "4").send();
    sponge.event("e3").set("label", "5").send();
    sponge.event("e3").set("label", "6").send();
    sponge.event("e3").set("label", "7").send();
}
