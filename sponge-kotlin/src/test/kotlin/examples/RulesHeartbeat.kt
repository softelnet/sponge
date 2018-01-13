/*
 * Sponge Knowledge base
 * Heartbeat
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.event.EventSchedulerEntry
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.kotlin.KTrigger
import org.openksavi.sponge.rule.Rule
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

class RulesHeartbeat : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("soundTheAlarm", AtomicBoolean(false))
    }

    class HeartbeatFilter : KFilter() {
        var heartbeatCounter = 0
        override fun onConfigure() = setEvent("heartbeat")
        override fun onAccept(event: Event): Boolean {
            heartbeatCounter++
            if (heartbeatCounter > 2) {
                eps.removeEvent(eps.getVariable("hearbeatEventEntry"))
                return false
            } else {
                return true
            }
        }
    }

    /** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
    class HeartbeatRule : KRule() {
        override fun onConfigure() {
            setEvents("heartbeat h1", "heartbeat h2 :none")
            addConditions("h2", { rule: Rule, event: Event -> rule.firstEvent.get<Any?>("source") == event.get<Any?>("source") })
            duration = Duration.ofSeconds(2)
        }

        override fun onRun(event: Event?) {
            eps.event("alarm").set("severity", 1).send()
        }
    }

    class AlarmTrigger : KTrigger() {
        override fun onConfigure() = setEvent("alarm")
        override fun onRun(event: Event) {
            println("Sound the alarm!")
            eps.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
        }
    }

    override fun onStartup() {
        eps.setVariable("hearbeatEventEntry",  eps.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
    }
}
