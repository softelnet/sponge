/*
 * Sponge Knowledge base
 * Generating events by Cron
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    EPS.setVariable("scheduleEntry", null)
    EPS.setVariable("eventCounter", AtomicInteger(0))
}

class CronTrigger : Trigger() {
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

fun onStartup() {
    // send event every 2 seconds
    EPS.setVariable("scheduleEntry", EPS.event("cronEvent").sendAt("0/2 * * * * ?"))
}
