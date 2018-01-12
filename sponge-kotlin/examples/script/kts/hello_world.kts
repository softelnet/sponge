/*
 * Sponge Knowledge base
 * Hello world
 */

class HelloWorld : Trigger() {
    override fun onConfigure() {
        setEvent("helloEvent")
    }

    override fun onRun(event: Event?) {
        println(event?.get("say"))
    }
}

fun onStartup() {
    EPS.event("helloEvent").set("say", "Hello World!").send()
}