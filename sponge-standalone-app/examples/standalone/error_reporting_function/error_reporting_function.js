/**
 * Sponge Knowledge Base 
 * Error reporting
 */

var HelloWorldTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("helloEvent");
    },
    onRun : function(self, event) {
        print(event.get("say"));
    }
});

function onStartup() {
    whatIsThis.doSomething();
    sponge.event("helloEvent").set("say", "Hello World!").send();
}
