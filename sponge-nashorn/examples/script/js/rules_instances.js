/**
 * Sponge Knowledge base
 * Rules - instances
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("countA", new AtomicInteger(0));
    sponge.setVariable("countB", new AtomicInteger(0));
    sponge.setVariable("max", 100);
}

var RuleA = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["a a1", "a a2"]);
    },
    onRun: function(self, event) {
        sponge.getVariable("countA").incrementAndGet();
    }
});

var RuleB = Java.extend(Rule, {
    onConfigure: function(self) {
        self.withEvents(["b b1", "b b2"]);
    },
    onRun: function(self, event) {
        sponge.getVariable("countB").incrementAndGet();
    }
});


function onStartup() {
    for (i = 0; i < sponge.getVariable("max"); i++) { 
        sponge.event("a").send();
        sponge.event("b").send();
    }
}

