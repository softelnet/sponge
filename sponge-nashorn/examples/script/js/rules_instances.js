/**
 * Sponge Knowledge base
 * Rules - instances
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("countA", new AtomicInteger(0));
    EPS.setVariable("countB", new AtomicInteger(0));
    EPS.setVariable("max", 100);
}

var RuleA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["a a1", "a a2"];
    },
    onRun: function(self, event) {
        EPS.getVariable("countA").incrementAndGet();
    }
});

var RuleB = Java.extend(Rule, {
    onConfigure: function(self) {
        self.events = ["b b1", "b b2"];
    },
    onRun: function(self, event) {
        EPS.getVariable("countB").incrementAndGet();
    }
});


function onStartup() {
    for (i = 0; i < EPS.getVariable("max"); i++) { 
        EPS.event("a").send();
        EPS.event("b").send();
    }
}

