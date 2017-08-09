/**
 * Sponge Knowledge base
 * Heartbeat 2
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    EPS.setVariable("soundTheAlarm", new AtomicBoolean(false))
}

// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule extends Rule {
    void onConfigure() {
        this.events = ["heartbeat h1", "heartbeat h2 :none"]
        this.duration = Duration.ofSeconds(2)
    }
    void onRun(Event event) {
        this.logger.info("Sound the alarm!")
        EPS.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    EPS.event("heartbeat").send()
    EPS.event("heartbeat").sendAfter(1000)
}
