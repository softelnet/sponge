/*
 * Sponge Knowledge Base
 * Using java filters
 */

import org.openksavi.sponge.examples.ShapeFilter
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
    eventCounter.put("e1", AtomicInteger(0))
    eventCounter.put("e2", AtomicInteger(0))
    eventCounter.put("e3", AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

class FilterTrigger : Trigger() {
    override fun onConfigure() {
        withEvents("e1", "e2", "e3")
    }
    override fun onRun(event: Event) {
        logger.debug("Processing trigger for event {}", event)
        sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.name)!!.incrementAndGet()
    }
}

fun onLoad() {
    sponge.enableJava(ShapeFilter::class.java)
}

fun onStartup() {
    sponge.event("e1").sendAfter(100, 100)
    sponge.event("e2").set("shape", "square").sendAfter(200, 100)
    sponge.event("e3").set("shape", "circle").sendAfter(300, 100)
}
