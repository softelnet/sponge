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
        sponge.setVariable("soundTheAlarm", AtomicBoolean(false))
    }

    class HeartbeatFilter : KFilter() {
        var heartbeatCounter = 0
        override fun onConfigure() = setEvent("heartbeat")
        override fun onAccept(event: Event): Boolean {
            heartbeatCounter++
            if (heartbeatCounter > 2) {
                sponge.removeEvent(sponge.getVariable("hearbeatEventEntry"))
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
            sponge.event("alarm").set("severity", 1).send()
        }
    }

    class AlarmTrigger : KTrigger() {
        override fun onConfigure() = setEvent("alarm")
        override fun onRun(event: Event) {
            println("Sound the alarm!")
            sponge.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
        }
    }

    override fun onStartup() {
        sponge.setVariable("hearbeatEventEntry",  sponge.event("heartbeat").set("source", "Host1").sendAfter(100, 1000))
    }
}
