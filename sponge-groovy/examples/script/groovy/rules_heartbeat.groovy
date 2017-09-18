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

    void onConfigure() {
        this.event = "heartbeat"
    }
    boolean onAccept(Event event) {
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
    void onConfigure() {
        this.events = ["heartbeat h1", "heartbeat h2 :none"]
        this.addConditions("h2", { rule, event ->
                return rule.firstEvent.get("source") == event.get("source")
        })
        this.duration = Duration.ofSeconds(2)
    }
    void onRun(Event event) {
        EPS.event("alarm").set("severity", 1).send()
    }
}

class AlarmTrigger extends Trigger {
    void onConfigure() {
        this.event = "alarm"
    }
    void onRun(Event event) {
        println "Sound the alarm!"
        EPS.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    EPS.setVariable("hearbeatEventEntry", EPS.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
}
