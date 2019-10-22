/**
 * Sponge Knowledge base
 * Using filter builders
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("blue", new AtomicInteger(0))
    eventCounter.put("red", new AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

class ColorTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("e1")
    }
    void onRun(Event event) {
        this.logger.debug("Received event {}", event)
        sponge.getVariable("eventCounter").get(event.get("color")).incrementAndGet()
    }
}

void onLoad() {
    sponge.enable(new FilterBuilder("ColorFilter").withEvent("e1").withOnAccept({ filter, event ->
        sponge.logger.debug("Received event {}", event)
        String color = event.get("color", null)
        if (color == null || color != "blue") {
            sponge.logger.debug("rejected")
            return false
        } else {
            sponge.logger.debug("accepted")
            return true
        }
    }))
}

void onStartup() {
    sponge.event("e1").send()
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
}
