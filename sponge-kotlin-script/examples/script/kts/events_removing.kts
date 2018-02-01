/*
 * Sponge Knowledge base
 * Removing scheduled events
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    EPS.setVariable("eventCounter", AtomicInteger(0))
    EPS.setVariable("allowNumber", 2)
}

class Trigger1 : Trigger() {
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

class Trigger2 : Trigger() {
    override fun onConfigure() = setEvent("e2")
    override fun onRun(event: Event) {
        logger.debug("Removing entry")
        eps.removeEvent(eps.getVariable("eventEntry"))
    }
}

fun onStartup() {
    val start = 500L
    val interval = 1000L
    EPS.setVariable("eventEntry", EPS.event("e1").sendAfter(start, interval))
    EPS.event("e2").sendAfter(interval * EPS.getVariable<Int>("allowNumber"))
}
