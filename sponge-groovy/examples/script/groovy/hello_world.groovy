/**
 * Sponge Knowledge base
 * Hello world
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
    EPS.event("helloEvent").set("say", "Hello World!").send()
}
