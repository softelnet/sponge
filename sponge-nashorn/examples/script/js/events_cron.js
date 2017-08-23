/**
 * Sponge Knowledge base
 * Generating events by Cron
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    EPS.setVariable("scheduleEntry", null);
    EPS.setVariable("eventCounter", new AtomicInteger(0));
}

var CronTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "cronEvent";
    },
    onRun: function(self, event) {
        eventCounter = EPS.getVariable("eventCounter")
        eventCounter.incrementAndGet();
        self.logger.debug("Received event {}: {}", eventCounter.get(), event.name);
        if (eventCounter.get() == 2) {
            self.logger.debug("removing scheduled event");
            EPS.removeEvent(EPS.getVariable("scheduleEntry"));
        }
    }
});

function onStartup() {
    // send event every 2 seconds
    EPS.setVariable("scheduleEntry", EPS.event("cronEvent").sendAt("0/2 * * * * ?"));
}
