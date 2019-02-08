/*
 * Sponge Knowledge base
 * Heartbeat
 */

import java.util.concurrent.atomic.AtomicBoolean

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("soundTheAlarm", AtomicBoolean(false))
}

class HeartbeatFilter : Filter() {
    var heartbeatCounter = 0
    override fun onConfigure() {
        withEvent("heartbeat")
    }
    override fun onAccept(event: Event): Boolean {
        heartbeatCounter++
        if (heartbeatCounter > 2) {
            sponge.removeEvent(sponge.getVariable("hearbeatEventEntry"))
            return false
        } else {
            return true
        }
    }
}

/** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
class HeartbeatRule : Rule() {
    override fun onConfigure() {
        withEvents("heartbeat h1", "heartbeat h2 :none")
        withConditions("h2", { rule: Rule, event: Event -> rule.firstEvent.get<Any?>("source") == event.get<Any?>("source") })
        withDuration(Duration.ofSeconds(2))
    }

    override fun onRun(event: Event?) {
        sponge.event("alarm").set("severity", 1).send()
    }
}

class AlarmTrigger : Trigger() {
    override fun onConfigure() {
        withEvent("alarm")
    }
    override fun onRun(event: Event) {
        println("Sound the alarm!")
        sponge.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
    }
}

fun onStartup() {
    sponge.setVariable("hearbeatEventEntry", sponge.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
}
