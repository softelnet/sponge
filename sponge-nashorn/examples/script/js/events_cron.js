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
    configure: function(self) {
        self.event = "cronEvent";
    },
    run: function(self, event) {
        eventCounter = EPS.getVariable("eventCounter")
        self.logger.debug("Received event {}: {}", eventCounter.get() + 1, event.name);
        if (eventCounter.get() + 1 == 2) {
            self.logger.debug("removing scheduled event");
            EPS.removeEvent(EPS.getVariable("scheduleEntry"));
        }
        eventCounter.incrementAndGet();
    }
});

function onStartup() {
    // send event every second
    EPS.setVariable("scheduleEntry", EPS.event("cronEvent").sendAt("0/1 * * * * ?"));
}
