/**
 * Sponge Knowledge base
 * Using filters for deduplication of events.
 */

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit

import org.openksavi.sponge.core.library.Deduplication

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("e1-blue", new AtomicInteger(0))
    eventCounter.put("e1-red", new AtomicInteger(0))
    eventCounter.put("e2-blue", new AtomicInteger(0))
    eventCounter.put("e2-red", new AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

class ColorDeduplicationFilter extends Filter {
    def deduplication = new Deduplication("color")
    void onConfigure() {
        this.event = "e1"
    }
    void onInit() {
        this.deduplication.cacheBuilder.maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES)
    }
    boolean onAccept(Event event) {
        return this.deduplication.onAccept(event)
    }
}

class ColorTrigger extends Trigger {
    void onConfigure() {
        this.events = ["e1", "e2"]
    }
    void onRun(Event event) {
        this.logger.debug("Received event {}", event)
        sponge.getVariable("eventCounter").get(event.name + "-" + event.get("color")).incrementAndGet()
    }
}

void onStartup() {
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
    sponge.event("e2").set("color", "red").send()
    sponge.event("e2").set("color", "blue").send()

    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
    sponge.event("e2").set("color", "red").send()
    sponge.event("e2").set("color", "blue").send()
}
