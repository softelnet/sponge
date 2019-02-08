/**
 * Sponge Knowledge base
 * Heartbeat
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    sponge.setVariable("hearbeatEventEntry", null)
    sponge.setVariable("soundTheAlarm", new AtomicBoolean(false))
}

class HeartbeatFilter extends Filter {
    int heartbeatCounter = 0

    void onConfigure() {
        this.withEvent("heartbeat")
    }
    boolean onAccept(Event event) {
        this.heartbeatCounter += 1
        if (this.heartbeatCounter > 2) {
            sponge.removeEvent(sponge.getVariable("hearbeatEventEntry"))
            return false
        } else {
            return true
        }
    }
}

// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule extends Rule {
    void onConfigure() {
        this.withEvents(["heartbeat h1", "heartbeat h2 :none"])
        this.withCondition("h2", { rule, event ->
                return rule.firstEvent.get("source") == event.get("source")
        })
        this.withDuration(Duration.ofSeconds(2))
    }
    void onRun(Event event) {
        sponge.event("alarm").set("severity", 1).send()
    }
}

class AlarmTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("alarm")
    }
    void onRun(Event event) {
        println "Sound the alarm!"
        sponge.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    sponge.setVariable("hearbeatEventEntry", sponge.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
}
