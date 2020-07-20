/*
 * Sponge Knowledge Base
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
        sponge.setVariable("value", AtomicReference(""))
    }

    class A : KTrigger() {
        override fun onConfigure() {
            withEvent("a")
        }
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(1)
            sponge.getVariable<AtomicReference<Any>>("value").set("A1")
            TimeUnit.SECONDS.sleep(3)
            sponge.getVariable<AtomicReference<Any>>("value").set("A2")
        }
    }

    class B : KTrigger() {
        override fun onConfigure() {
            withEvent("b")
        }
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(2)
            sponge.getVariable<AtomicReference<Any>>("value").set("B1")
            TimeUnit.SECONDS.sleep(4)
            sponge.getVariable<AtomicReference<Any>>("value").set("B2")
        }
    }

    class C : KTrigger() {
        override fun onConfigure() {
            withEvent("c")
        }
        override fun onRun(event: Event) {
            TimeUnit.SECONDS.sleep(8)
            sponge.getVariable<AtomicReference<Any>>("value").set("C1")
            TimeUnit.SECONDS.sleep(1)
            sponge.getVariable<AtomicReference<Any>>("value").set("C2")
        }
    }

    override fun onStartup() {
        sponge.event("a").send()
        sponge.event("b").send()
        sponge.event("c").send()
    }
}
