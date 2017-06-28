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
    void configure() {
        this.events = ["heartbeat h1", "heartbeat h2 :none"]
        this.duration = Duration.ofSeconds(2)
    }
    void run(Event event) {
        this.logger.info("Sound the alarm!")
        EPS.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    EPS.event("heartbeat").sendAfter(100)
    EPS.event("heartbeat").sendAfter(1000)
}
