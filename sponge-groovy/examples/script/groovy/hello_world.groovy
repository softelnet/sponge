/**
 * Sponge Knowledge base
 * Hello world
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
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
