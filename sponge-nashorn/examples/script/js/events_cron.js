/**
 * Sponge Knowledge base
 * Generating events by Cron
 */

var AtomicInteger = java.util.concurrent.atomic.AtomicInteger;

function onInit() {
    sponge.setVariable("scheduleEntry", null);
    sponge.setVariable("eventCounter", new AtomicInteger(0));
}

var CronTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "cronEvent";
    },
    onRun: function(self, event) {
        eventCounter = sponge.getVariable("eventCounter")
        eventCounter.incrementAndGet();
        self.logger.debug("Received event {}: {}", eventCounter.get(), event.name);
        if (eventCounter.get() == 2) {
            self.logger.debug("removing scheduled event");
            sponge.removeEvent(sponge.getVariable("scheduleEntry"));
        }
    }
});

function onStartup() {
    // send event every 2 seconds
    sponge.setVariable("scheduleEntry", sponge.event("cronEvent").sendAt("0/2 * * * * ?"));
}
