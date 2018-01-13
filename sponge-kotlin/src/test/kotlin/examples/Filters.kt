/*
 * Sponge Knowledge base
 * Using filters
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.PowerEchoAction
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class Filters : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
        eventCounter.put("blue", AtomicInteger(0))
        eventCounter.put("red", AtomicInteger(0))
        eps.setVariable("eventCounter", eventCounter)
    }

    class ColorFilter : KFilter() {
        override fun onConfigure() = setEvent("e1")
        override fun onAccept(event: Event): Boolean {
            logger.debug("Received event {}", event)
            val color: String? = event.get("color")
            if (color == null || color != "blue") {
                logger.debug("rejected")
                return false
            } else {
                logger.debug("accepted")
                return true
            }
        }
    }

    class ColorTrigger : KTrigger() {
        override fun onConfigure() = setEvent("e1")
        override fun onRun(event: Event) {
            logger.debug("Received event {}", event)
            eps.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.get<String>("color"))!!.incrementAndGet()
        }
    }

    override fun onStartup() {
        eps.event("e1").send()
        eps.event("e1").set("color", "red").send()
        eps.event("e1").set("color", "blue").send()
    }
}
