/*
 * Sponge Knowledge Base
 * Using java filters
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.ShapeFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class FiltersJava : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        var eventCounter = Collections.synchronizedMap(HashMap<String, AtomicInteger>())
        eventCounter.put("e1", AtomicInteger(0))
        eventCounter.put("e2", AtomicInteger(0))
        eventCounter.put("e3", AtomicInteger(0))
        sponge.setVariable("eventCounter", eventCounter)
    }

    class FilterTrigger : KTrigger() {
        override fun onConfigure() {
            withEvents("e1", "e2", "e3")
        }
        override fun onRun(event: Event) {
            logger.debug("Processing trigger for event {}", event)
            sponge.getVariable<Map<String, AtomicInteger>>("eventCounter").get(event.name)!!.incrementAndGet()
        }
    }

    override fun onLoad() {
        sponge.enableJava(ShapeFilter::class.java)
    }

    override fun onStartup() {
        sponge.event("e1").sendAfter(100, 100)
        sponge.event("e2").set("shape", "square").sendAfter(200, 100)
        sponge.event("e3").set("shape", "circle").sendAfter(300, 100)
    }
}
