/**
 * Sponge Knowledge base
 * Error reporting
 */

class HelloWorldTrigger extends Trigger {
    void configure() {
        this.eventName = "helloEvent"
    }
    void run(Event event) {
        whatIsThis.doSomething()
        println event.get("say")
    }
}

void onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
