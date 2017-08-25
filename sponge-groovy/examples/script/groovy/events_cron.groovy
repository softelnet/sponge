/**
 * Sponge Knowledge base
 * Generating events by Cron
 */

void onInit() {
    EPS.setVariable("eventCounter", 0)
}

class CronTrigger extends Trigger {
    void onConfigure() {
        this.event = "cronEvent"
    }
    void onRun(Event event) {
        int eventCounter = EPS.getVariable("eventCounter")
        eventCounter += 1
        EPS.setVariable("eventCounter", eventCounter)
        this.logger.debug("Received event {}: {}", eventCounter, event.name)
        if (eventCounter == 2) {
            this.logger.debug("removing scheduled event")
            EPS.removeEvent(EPS.getVariable("scheduleEntry"))
        }
    }
}

void onStartup() {
    // send event every 2 seconds
    EPS.setVariable("scheduleEntry", EPS.event("cronEvent").sendAt("0/2 * * * * ?"))
}
