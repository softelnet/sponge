/*
 * Sponge Knowledge Base
 * Removing scheduled events
 */

import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    sponge.setVariable("eventCounter", AtomicInteger(0))
    sponge.setVariable("allowNumber", 2)
}

class Trigger1 : Trigger() {
    override fun onConfigure() {
        withEvent("e1")
    }
    override fun onRun(event: Event) {
        var eventCounter: AtomicInteger = sponge.getVariable("eventCounter")
        eventCounter.incrementAndGet()
        logger.debug("Received event {}, counter: {}", event.name, eventCounter)
        if (eventCounter.get() > sponge.getVariable<Int>("allowNumber")) {
            logger.debug("This line should not be displayed!")
        }
    }
}

class Trigger2 : Trigger() {
    override fun onConfigure() {
        withEvent("e2")
    }
    override fun onRun(event: Event) {
        logger.debug("Removing entry")
        sponge.removeEvent(sponge.getVariable("eventEntry"))
    }
}

fun onStartup() {
    val start = 500L
    val interval = 1000L
    sponge.setVariable("eventEntry", sponge.event("e1").sendAfter(start, interval))
    sponge.event("e2").sendAfter(interval * sponge.getVariable<Int>("allowNumber"))
}
