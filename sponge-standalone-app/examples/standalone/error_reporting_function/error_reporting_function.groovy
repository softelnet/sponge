/**
 * Sponge Knowledge Base
 * Error reporting
 */

class HelloWorldTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("helloEvent")
    }
    void onRun(Event event) {
        println event.get("say")
    }
}

void onStartup() {
    whatIsThis.doSomething()
    sponge.event("helloEvent").set("say", "Hello World!").send()
}
