/*
 * Sponge Knowledge base
 * Using filters
 */

import org.openksavi.sponge.examples.PowerEchoAction
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
    eventCounter.put("blue", AtomicInteger(0))
    eventCounter.put("red", AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}

class ColorFilter : Filter() {
    override fun onConfigure() {
        withEvent("e1")
    }
    override fun onAccept(event: Event): Boolean {
        logger.debug("Received event {}", event)
        val color: String? = event.get("color", null)
        if (color == null || color != "blue") {
            logger.debug("rejected")
            return false
        } else {
            logger.debug("accepted")
            return true
        }
    }
}

class ColorTrigger : Trigger() {
    override fun onConfigure() {
        withEvent("e1")
    }
    override fun onRun(event: Event) {
        logger.debug("Received event {}", event)
        sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.get<String>("color"))!!.incrementAndGet()
    }
}

fun onStartup() {
    sponge.event("e1").send()
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
}
