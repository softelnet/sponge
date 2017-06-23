/**
 * Sponge Knowledge base
 * Heartbeat
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    EPS.setVariable("hearbeatEventEntry", null)
    EPS.setVariable("soundTheAlarm", new AtomicBoolean(false))
}

class HeartbeatFilter extends Filter {
    int heartbeatCounter = 0

    void configure() {
        this.eventName = "heartbeat"
    }
    boolean accepts(Event event) {
        this.heartbeatCounter += 1
        if (this.heartbeatCounter > 2) {
            EPS.removeEvent(EPS.getVariable("hearbeatEventEntry"))
            return false
        } else {
            return true
        }
    }
}

// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule extends Rule {
    void configure() {
        this.events = ["heartbeat h1", "heartbeat h2 :none"]
        this.duration = Duration.ofSeconds(2)
    }
    void run(Event event) {
        EPS.event("alarm").set("severity", 1).send()
    }
}

class AlarmTrigger extends Trigger {
    void configure() {
        this.eventName = "alarm"
    }
    void run(Event event) {
        println "Sound the alarm!"
        EPS.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    EPS.setVariable("hearbeatEventEntry", EPS.event("heartbeat").sendAfter(100, 1000))
}
