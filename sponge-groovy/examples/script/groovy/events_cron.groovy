/**
 * Sponge Knowledge base
 * Generating events by Cron
 */

void onInit() {
    sponge.setVariable("eventCounter", 0)
}

class CronTrigger extends Trigger {
    void onConfigure() {
        this.event = "cronEvent"
    }
    void onRun(Event event) {
        int eventCounter = sponge.getVariable("eventCounter")
        eventCounter += 1
        sponge.setVariable("eventCounter", eventCounter)
        this.logger.debug("Received event {}: {}", eventCounter, event.name)
        if (eventCounter == 2) {
            this.logger.debug("removing scheduled event")
            sponge.removeEvent(sponge.getVariable("scheduleEntry"))
        }
    }
}

void onStartup() {
    // send event every 2 seconds
    sponge.setVariable("scheduleEntry", sponge.event("cronEvent").sendAt("0/2 * * * * ?"))
}
