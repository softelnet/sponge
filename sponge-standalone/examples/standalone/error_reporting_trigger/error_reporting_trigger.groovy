/**
 * Sponge Knowledge base
 * Error reporting
 */

class HelloWorldTrigger extends Trigger {
    void onConfigure() {
        this.event = "helloEvent"
    }
    void onRun(Event event) {
        whatIsThis.doSomething()
        println event.get("say")
    }
}

void onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
