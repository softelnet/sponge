/**
 * Sponge Knowledge base
 * Concurrency
 */

var AtomicReference = java.util.concurrent.atomic.AtomicReference;
var TimeUnit = java.util.concurrent.TimeUnit;

function onInit() {
    // Variables for assertions only
    EPS.setVariable("value", new AtomicReference(""));
}

var A = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "a";
    },
    run: function(self, event) {
        TimeUnit.SECONDS.sleep(1);
        EPS.getVariable("value").set("A1");
        TimeUnit.SECONDS.sleep(3);
        EPS.getVariable("value").set("A2");
    }
});

var B = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "b";
    },
    run: function(self, event) {
        TimeUnit.SECONDS.sleep(2);
        EPS.getVariable("value").set("B1");
        TimeUnit.SECONDS.sleep(4);
        EPS.getVariable("value").set("B2");
    }
});

var C = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "c";
    },
    run: function(self, event) {
        TimeUnit.SECONDS.sleep(8);
        EPS.getVariable("value").set("C1");
        TimeUnit.SECONDS.sleep(1);
        EPS.getVariable("value").set("C2");
    }
});

function onStartup() {
    EPS.event("a").send();
    EPS.event("b").send();
    EPS.event("c").send();
}

