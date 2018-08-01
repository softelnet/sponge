/**
 * Sponge Knowledge base
 * Concurrency
 */

var AtomicReference = java.util.concurrent.atomic.AtomicReference;
var TimeUnit = java.util.concurrent.TimeUnit;

function onInit() {
    // Variables for assertions only
    sponge.setVariable("value", new AtomicReference(""));
}

var A = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "a";
    },
    onRun: function(self, event) {
        TimeUnit.SECONDS.sleep(1);
        sponge.getVariable("value").set("A1");
        TimeUnit.SECONDS.sleep(3);
        sponge.getVariable("value").set("A2");
    }
});

var B = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "b";
    },
    onRun: function(self, event) {
        TimeUnit.SECONDS.sleep(2);
        sponge.getVariable("value").set("B1");
        TimeUnit.SECONDS.sleep(4);
        sponge.getVariable("value").set("B2");
    }
});

var C = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "c";
    },
    onRun: function(self, event) {
        TimeUnit.SECONDS.sleep(8);
        sponge.getVariable("value").set("C1");
        TimeUnit.SECONDS.sleep(1);
        sponge.getVariable("value").set("C2");
    }
});

function onStartup() {
    sponge.event("a").send();
    sponge.event("b").send();
    sponge.event("c").send();
}

