/**
 * Sponge Knowledge base
 * Error reporting
 */

class HelloWorldTrigger extends Trigger {
    void configure() {
        this.event = "helloEvent"
    }
    void run(Event event) {
        println event.get("say")
    }
}

void onStartup() {
    whatIsThis.doSomething()
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
