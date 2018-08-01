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
    sponge.setVariable("eventCounter", eventCounter)
}

class FilterTrigger extends Trigger {
    void onConfigure() {
        this.setEvents("e1", "e2", "e3")
    }
    void onRun(Event event) {
        this.logger.debug("Processing trigger for event {}", event)
        sponge.getVariable("eventCounter").get(event.name).incrementAndGet()
    }
}

void onLoad() {
    sponge.enableJava(ShapeFilter)
}

void onStartup() {
    sponge.event("e1").sendAfter(100, 100)
    sponge.event("e2").set("shape", "square").sendAfter(200, 100)
    sponge.event("e3").set("shape", "circle").sendAfter(300, 100)
}
