/**
 * Sponge Knowledge base 
 * Hello world
 */

var HelloWorldTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "helloEvent";
    },
    onRun : function(self, event) {
        print(event.get("say"));
    }
});

function onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send();
}
