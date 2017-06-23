/**
 * Sponge Knowledge base
 * Using java filters 
 */

import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.examples.ShapeFilter

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("e1", new AtomicInteger(0))
    eventCounter.put("e2", new AtomicInteger(0))
    eventCounter.put("e3", new AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)
}

class FilterTrigger extends Trigger {
    void configure() {
        this.setEventNames("e1", "e2", "e3")
    }
    void run(Event event) {
        this.logger.debug("Processing trigger for event {}", event)
        EPS.getVariable("eventCounter").get(event.name).incrementAndGet()
    }
}

void onLoad() {
    EPS.enableJava(ShapeFilter)
}

void onStartup() {
    EPS.event("e1").sendAfter(100, 100)
    EPS.event("e2").set("shape", "square").sendAfter(200, 100)
    EPS.event("e3").set("shape", "circle").sendAfter(300, 100)
}
