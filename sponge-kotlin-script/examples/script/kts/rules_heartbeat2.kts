/*
 * Sponge Knowledge base
 * Heartbeat 2
 */

import java.util.concurrent.atomic.AtomicBoolean

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("soundTheAlarm", AtomicBoolean(false))
}

/** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
class HeartbeatRule : Rule() {
    override fun onConfigure() {
        setEvents("heartbeat h1", "heartbeat h2 :none")
        duration = Duration.ofSeconds(2)
    }

    override fun onRun(event: Event?) {
        logger.info("Sound the alarm!")
        sponge.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
    }
}


fun onStartup() {
    sponge.event("heartbeat").send()
    sponge.event("heartbeat").sendAfter(1000)
}
