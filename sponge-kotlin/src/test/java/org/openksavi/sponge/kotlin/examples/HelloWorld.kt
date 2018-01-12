package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KotlinKnowledgeBase
import org.openksavi.sponge.kotlin.KotlinTrigger

class HelloWorld : KotlinKnowledgeBase() {

    class HelloWorld : KotlinTrigger() {
        override fun onConfigure() {
            setEvent("helloEvent")
        }

        override fun onRun(event: Event?) {
            println(event?.get("say"))
        }
    }

    override fun onStartup() {
        eps.event("helloEvent").set("say", "Hello World!").send()
    }
}