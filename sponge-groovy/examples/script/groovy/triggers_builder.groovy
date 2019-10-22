/**
 * Sponge Knowledge base
 * Trigger builders
 */

import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    sponge.setVariable("receivedEventA", new AtomicBoolean(false))
    sponge.setVariable("receivedEventBCount", new AtomicInteger(0))
}

void onLoad() {
    sponge.enable(new TriggerBuilder("TriggerA").withEvent("a").withOnRun({ trigger, event ->
        trigger.logger.debug("Received event: {}", event.name)
        sponge.getVariable("receivedEventA").set(true)
    }))

    sponge.enable(new TriggerBuilder("TriggerB").withEvent("b").withOnRun({ trigger, event ->
        trigger.logger.debug("Received event: {}", event.name)
        def receivedEventBCount = sponge.getVariable("receivedEventBCount")
        if (receivedEventBCount.get() == 0) {
            trigger.logger.debug("Statistics: {}", sponge.statisticsSummary)
        }
        receivedEventBCount.incrementAndGet()
    }))
}

void onStartup() {
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name)
    sponge.event("a").send()
    sponge.event("b").sendAfter(200, 200)
}
