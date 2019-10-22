/**
 * Sponge Knowledge base
 * Trigger builders
 */

var AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean
var AtomicInteger = java.util.concurrent.atomic.AtomicInteger

function onInit() {
    // Variables for assertions only
    sponge.setVariable("receivedEventA", new AtomicBoolean(false))
    sponge.setVariable("receivedEventBCount", new AtomicInteger(0))
}

function onLoad() {
    sponge.enable(new TriggerBuilder("TriggerA").withEvent("a").withOnRun(function (trigger, event) {
        trigger.logger.debug("Received event: {}", event.name)
        sponge.getVariable("receivedEventA").set(true)
    }))

    sponge.enable(new TriggerBuilder("TriggerB").withEvent("b").withOnRun(function (trigger, event) {
        trigger.logger.debug("Received event: {}", event.name)
        if (sponge.getVariable("receivedEventBCount").get() == 0) {
            trigger.logger.debug("Statistics: {}", sponge.statisticsSummary)
        }
        sponge.getVariable("receivedEventBCount").incrementAndGet()
    }))
}

function onStartup() {
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name)
    sponge.event("a").send()
    sponge.event("b").sendAfter(200, 200)
}
