/*
 * Sponge Knowledge base
 * Using filters for deduplication of events.
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.core.library.Deduplication
import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class FiltersDeduplication : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
        eventCounter.put("e1-blue", AtomicInteger(0))
        eventCounter.put("e1-red", AtomicInteger(0))
        eventCounter.put("e2-blue", AtomicInteger(0))
        eventCounter.put("e2-red", AtomicInteger(0))
        sponge.setVariable("eventCounter", eventCounter)
    }


    class ColorDeduplicationFilter : KFilter() {
        lateinit var deduplication: Deduplication

        override fun onConfigure() = setEvent("e1")

        override fun onInit() {
            deduplication = Deduplication("color").also {
                it.cacheBuilder.maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES)
            }
        }

        override fun onAccept(event: Event) = deduplication.onAccept(event)
    }

    class ColorTrigger : KTrigger() {
        override fun onConfigure() = setEvents("e1", "e2")

        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.name + "-" + event.get<String>("color"))!!.incrementAndGet()
        }
    }

    override fun onStartup() {
        sponge.event("e1").set("color", "red").send()
        sponge.event("e1").set("color", "blue").send()
        sponge.event("e2").set("color", "red").send()
        sponge.event("e2").set("color", "blue").send()

        sponge.event("e1").set("color", "red").send()
        sponge.event("e1").set("color", "blue").send()
        sponge.event("e2").set("color", "red").send()
        sponge.event("e2").set("color", "blue").send()
    }
}
