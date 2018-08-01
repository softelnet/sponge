/*
 * Sponge Knowledge base
 * Using filters for deduplication of events.
 */

import org.openksavi.sponge.core.library.Deduplication
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
    eventCounter.put("e1-blue", AtomicInteger(0))
    eventCounter.put("e1-red", AtomicInteger(0))
    eventCounter.put("e2-blue", AtomicInteger(0))
    eventCounter.put("e2-red", AtomicInteger(0))
    sponge.setVariable("eventCounter", eventCounter)
}


class ColorDeduplicationFilter : Filter() {
    lateinit var deduplication: Deduplication

    override fun onConfigure() = setEvent("e1")

    override fun onInit() {
        deduplication = Deduplication("color").also {
            it.cacheBuilder.maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES)
        }
    }

    override fun onAccept(event: Event) = deduplication.onAccept(event)
}

class ColorTrigger : Trigger() {
    override fun onConfigure() = setEvents("e1", "e2")

    override fun onRun(event: Event) {
        logger.debug("Received event {}", event)
        sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.name + "-" + event.get<String>("color"))!!.incrementAndGet()
    }
}

fun onStartup() {
    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
    sponge.event("e2").set("color", "red").send()
    sponge.event("e2").set("color", "blue").send()

    sponge.event("e1").set("color", "red").send()
    sponge.event("e1").set("color", "blue").send()
    sponge.event("e2").set("color", "red").send()
    sponge.event("e2").set("color", "blue").send()
}
