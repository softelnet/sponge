/*
 * Sponge Knowledge base
 * Using filter builders
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import org.openksavi.sponge.kotlin.KFilterBuilder

class FiltersBuilder : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
        eventCounter.put("blue", AtomicInteger(0))
        eventCounter.put("red", AtomicInteger(0))
        sponge.setVariable("eventCounter", eventCounter)
    }

    class ColorTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("e1")
        }

        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.get<String>("color"))!!.incrementAndGet()
        }
    }

    override fun onLoad() {
        sponge.enable(KFilterBuilder("ColorFilter").withEvent("e1").withOnAccept({ filter, event ->
            sponge.logger.debug("Received event {}", event)
            val color: String? = event.get("color", null)
            if (color == null || color != "blue") {
                sponge.logger.debug("rejected")
                false
            } else {
                sponge.logger.debug("accepted")
                true
            }
        }))
    }

    override fun onStartup() {
        sponge.event("e1").send()
        sponge.event("e1").set("color", "red").send()
        sponge.event("e1").set("color", "blue").send()
    }
}
