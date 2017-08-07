/**
 * Sponge Knowledge base
 * Using filters
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("blue", new AtomicInteger(0))
    eventCounter.put("red", new AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)
}

class ColorFilter extends Filter {
    void onConfigure() {
        this.event = "e1"
    }
    boolean onAccept(Event event) {
        this.logger.debug("Received event {}", event)
        String color = event.get("color")
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
        this.event = "e1"
    }
    void onRun(Event event) {
        this.logger.debug("Received event {}", event)
        EPS.getVariable("eventCounter").get(event.get("color")).incrementAndGet()
    }
}

void onStartup() {
    EPS.event("e1").send()
    EPS.event("e1").set("color", "red").send()
    EPS.event("e1").set("color", "blue").send()
}
