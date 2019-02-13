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
        sponge.setVariable("scheduleEntry", null)
        sponge.setVariable("eventCounter", AtomicInteger(0))
    }

    class CronTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("cronEvent")
        }
        override fun onRun(event: Event) {
            var eventCounter: AtomicInteger = sponge.getVariable("eventCounter")
            eventCounter.incrementAndGet()
            logger.debug("Received event {}: {}", eventCounter.get(), event.name)
            if (eventCounter.get() == 2) {
                logger.debug("removing scheduled event")
                sponge.removeEvent(sponge.getVariable("scheduleEntry"))
            }
        }
    }

    override fun onStartup() {
        // send event every 2 seconds
        sponge.setVariable("scheduleEntry", sponge.event("cronEvent").sendAt("0/2 * * * * ?"))
    }
}
