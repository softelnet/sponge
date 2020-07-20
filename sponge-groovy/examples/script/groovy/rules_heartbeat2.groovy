/**
 * Sponge Knowledge Base
 * Heartbeat 2
 */

import java.util.concurrent.atomic.AtomicBoolean

void onInit() {
    sponge.setVariable("soundTheAlarm", new AtomicBoolean(false))
}

// Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule extends Rule {
    void onConfigure() {
        this.withEvents(["heartbeat h1", "heartbeat h2 :none"]).withDuration(Duration.ofSeconds(2))
    }
    void onRun(Event event) {
        this.logger.info("Sound the alarm!")
        sponge.getVariable("soundTheAlarm").set(true)
    }
}

void onStartup() {
    sponge.event("heartbeat").send()
    sponge.event("heartbeat").sendAfter(1000)
}
