/*
 * Sponge Knowledge base
 * Heartbeat
 */

import java.util.concurrent.atomic.AtomicBoolean

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("soundTheAlarm", AtomicBoolean(false))
}

class HeartbeatFilter : Filter() {
    var heartbeatCounter = 0
    override fun onConfigure() = setEvent("heartbeat")
    override fun onAccept(event: Event): Boolean {
        heartbeatCounter++
        if (heartbeatCounter > 2) {
            eps.removeEvent(eps.getVariable("hearbeatEventEntry"))
            return false
        } else {
            return true
        }
    }
}

/** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
class HeartbeatRule : Rule() {
    override fun onConfigure() {
        setEvents("heartbeat h1", "heartbeat h2 :none")
        addConditions("h2", { rule: Rule, event: Event -> rule.firstEvent.get<Any?>("source") == event.get<Any?>("source") })
        duration = Duration.ofSeconds(2)
    }

    override fun onRun(event: Event?) {
        eps.event("alarm").set("severity", 1).send()
    }
}

class AlarmTrigger : Trigger() {
    override fun onConfigure() = setEvent("alarm")
    override fun onRun(event: Event) {
        println("Sound the alarm!")
        eps.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
    }
}

fun onStartup() {
    EPS.setVariable("hearbeatEventEntry", EPS.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
}
