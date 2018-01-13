/*
 * Sponge Knowledge base
 * Generating events by Cron
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.concurrent.atomic.AtomicInteger

class EventsCron : KKnowledgeBase() {

    override fun onInit() {
        eps.setVariable("scheduleEntry", null)
        eps.setVariable("eventCounter", AtomicInteger(0))
    }

    class CronTrigger : KTrigger() {
        override fun onConfigure() = setEvent("cronEvent")
        override fun onRun(event: Event) {
            var eventCounter: AtomicInteger = eps.getVariable("eventCounter")
            eventCounter.incrementAndGet()
            logger.debug("Received event {}: {}", eventCounter.get(), event.name)
            if (eventCounter.get() == 2) {
                logger.debug("removing scheduled event")
                eps.removeEvent(eps.getVariable("scheduleEntry"))
            }
        }
    }

    override fun onStartup() {
        // send event every 2 seconds
        eps.setVariable("scheduleEntry", eps.event("cronEvent").sendAt("0/2 * * * * ?"))
    }
}
