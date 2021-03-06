/*
 * Sponge Knowledge Base
 * Deep and shallow event clone policy.
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.event.EventClonePolicy
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import java.util.Collections

class EventsClonePolicy : KKnowledgeBase() {
    var events = Collections.synchronizedMap(HashMap<String, MutableList<Event>>())

    override fun onInit() {
        // Variables for assertions only
        events.put("defaultClonePolicy", ArrayList())
        events.put("deepClonePolicy", ArrayList())
        events.put("shallowClonePolicy", ArrayList())
        sponge.setVariable("events", events)
    }

    class ClonePolicyTrigger : KTrigger() {
        override fun onConfigure() {
            withEvents("defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy")
        }

        override fun onRun(event: Event) {
            var events: Map<String, MutableList<Event>> = sponge.getVariable("events")

            events.get(event.name)!!.add(event)
            logger.debug("Processing event: {}", event.name)
            var map: MutableMap<String, Any> = event.get("map")
            logger.debug("map attribute (before): {}", map)
            map.put("a", "Value " + events.get(event.name)!!.size)
            logger.debug("map attribute (after): {}", map)
        }
    }

    override fun onStartup() {
        fun setEventAttributes(event: Event) {
            val hash = HashMap<String, Any>()
            hash.put("a", "Value 0")
            hash.put("b", listOf(java.lang.Boolean.TRUE))
            event.set("map", hash)
            event.set("integer", Integer(10))
        }

        sponge.event("defaultClonePolicy").modify(::setEventAttributes).sendAfter(100, 1000)
        sponge.event("deepClonePolicy", EventClonePolicy.DEEP).modify(::setEventAttributes).sendAfter(200, 1000)
        sponge.event("shallowClonePolicy", EventClonePolicy.SHALLOW).modify(::setEventAttributes).sendAfter(400, 1000)
    }
}
