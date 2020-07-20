/*
 * Sponge Knowledge Base
 * Hello world
 */

class HelloWorld : Trigger() {
    override fun onConfigure() {
        withEvent("helloEvent")
    }

    override fun onRun(event: Event?) {
        println("${event?.get<String>("say")}")
    }
}

fun onStartup() {
    sponge.event("helloEvent").set("say", "Hello World!").send()
}
