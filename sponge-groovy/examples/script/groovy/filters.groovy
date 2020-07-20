/**
 * Sponge Knowledge Base
 * Using filters
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("blue", new AtomicInteger(0))
    eventCounter.put("red", new AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

class ColorFilter extends Filter {
    void onConfigure() {
        this.withEvent("e1")
    }
    boolean onAccept(Event event) {
        this.logger.debug("Received event {}", event)
        String color = event.get("color", null)
        if (color == null || color != "blue") {
            this.logger.debug("rejected")
            return false
        } else {
            this.logger.debug("accepted")
            return true
        }
    }
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

void onStartup() {
    sponge.event("e1").send()
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
}
