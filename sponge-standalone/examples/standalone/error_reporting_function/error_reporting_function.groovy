/**
 * Sponge Knowledge base
 * Error reporting
 */

class HelloWorldTrigger extends Trigger {
    void onConfigure() {
        this.event = "helloEvent"
    }
    void onRun(Event event) {
        println event.get("say")
    }
}

void onStartup() {
    whatIsThis.doSomething()
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
