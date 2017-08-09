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
        this.logger.debug("Received event {}: {}", eventCounter + 1, event.name)
        if (eventCounter + 1 == 2) {
            this.logger.debug("removing scheduled event")
            EPS.removeEvent(EPS.getVariable("scheduleEntry"))
        }
        eventCounter += 1
        EPS.setVariable("eventCounter", eventCounter)
    }
}

void onStartup() {
    // send event every second
    EPS.setVariable("scheduleEntry", EPS.event("cronEvent").sendAt("0/1 * * * * ?"))
}
