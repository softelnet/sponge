/*
 * Sponge Knowledge base
 * Removing scheduled events
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicInteger

class EventsRemoving : KKnowledgeBase() {

    override fun onInit() {
        eps.setVariable("eventCounter", AtomicInteger(0))
        eps.setVariable("allowNumber", 2)
    }

    class Trigger1 : KTrigger() {
        override fun onConfigure() = setEvent("e1")
        override fun onRun(event: Event) {
            var eventCounter: AtomicInteger = eps.getVariable("eventCounter")
            eventCounter.incrementAndGet()
            logger.debug("Received event {}, counter: {}", event.name, eventCounter)
            if (eventCounter.get() > eps.getVariable<Int>("allowNumber")) {
                logger.debug("This line should not be displayed!")
            }
        }
    }

    class Trigger2 : KTrigger() {
        override fun onConfigure() = setEvent("e2")
        override fun onRun(event: Event) {
            logger.debug("Removing entry")
            eps.removeEvent(eps.getVariable("eventEntry"))
        }
    }

    override fun onStartup() {
        val start = 500L
        val interval = 1000L
        eps.setVariable("eventEntry", eps.event("e1").sendAfter(start, interval))
        eps.event("e2").sendAfter(interval * eps.getVariable<Int>("allowNumber"))
    }
}
