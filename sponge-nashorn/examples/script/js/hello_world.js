/**
 * Sponge Knowledge base 
 * Hello world
 */

var HelloWorldTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.eventName = "helloEvent";
    },
    run : function(self, event) {
        print(event.get("say"));
    }
});

function onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send();
}
