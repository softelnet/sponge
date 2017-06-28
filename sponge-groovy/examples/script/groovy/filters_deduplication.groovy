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
    EPS.setVariable("eventCounter", eventCounter)
}

class ColorDeduplicationFilter extends Filter {
    def deduplication = new Deduplication("color")
    void configure() {
        this.eventName = "e1"
    }
    void init() {
        this.deduplication.cacheBuilder.maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES)
    }
    boolean accepts(Event event) {
        return this.deduplication.accepts(event)
    }
}

class ColorTrigger extends Trigger {
    void configure() {
        this.eventNames = ["e1", "e2"]
    }
    void run(Event event) {
        this.logger.debug("Received event {}", event)
        EPS.getVariable("eventCounter").get(event.name + "-" + event.get("color")).incrementAndGet()
    }
}

void onStartup() {
    EPS.event("e1").set("color", "red").sendAfter(100)
    EPS.event("e1").set("color", "blue").sendAfter(100)
    EPS.event("e2").set("color", "red").sendAfter(200)
    EPS.event("e2").set("color", "blue").sendAfter(200)

    EPS.event("e1").set("color", "red").sendAfter(300)
    EPS.event("e1").set("color", "blue").sendAfter(300)
    EPS.event("e2").set("color", "red").sendAfter(400)
    EPS.event("e2").set("color", "blue").sendAfter(400)
}
