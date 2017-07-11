/**
 * Sponge Knowledge base 
 * Error reporting
 */

var HelloWorldTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "helloEvent";
    },
    run : function(self, event) {
        print(event.get("say"));
    }
});

function onStartup() {
    whatIsThis.doSomething();
    EPS.event("helloEvent").set("say", "Hello World!").send();
}
