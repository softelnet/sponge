/**
 * Sponge Knowledge base
 * Triggers - Incorrect event pattern
 */

var TriggerAPattern = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("a.**");
    },
    onRun: function(self, event) {
    }
});
