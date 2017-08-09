/**
 * Sponge Knowledge base 
 * Error reporting
 */

var HelloWorldTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "helloEvent";
    },
    onRun : function(self, event) {
        whatIsThis.doSomething();
        print(event.get("say"));
    }
});

function onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send();
}
