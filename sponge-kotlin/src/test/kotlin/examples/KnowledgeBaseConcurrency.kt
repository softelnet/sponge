/*
 * Sponge Knowledge base
 * Concurrency
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class KnowledgeBaseConcurrency : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("value", AtomicReference(""))
    }

    class A : KTrigger() {
        override fun onConfigure() = setEvent("a")
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(1)
            eps.getVariable<AtomicReference<Any>>("value").set("A1")
            TimeUnit.SECONDS.sleep(3)
            eps.getVariable<AtomicReference<Any>>("value").set("A2")
        }
    }

    class B : KTrigger() {
        override fun onConfigure() = setEvent("b")
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(2)
            eps.getVariable<AtomicReference<Any>>("value").set("B1")
            TimeUnit.SECONDS.sleep(4)
            eps.getVariable<AtomicReference<Any>>("value").set("B2")
        }
    }

    class C : KTrigger() {
        override fun onConfigure() = setEvent("c")
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(8)
            eps.getVariable<AtomicReference<Any>>("value").set("C1")
            TimeUnit.SECONDS.sleep(1)
            eps.getVariable<AtomicReference<Any>>("value").set("C2")
        }
    }

    override fun onStartup() {
        eps.event("a").send()
        eps.event("b").send()
        eps.event("c").send()
    }
}
