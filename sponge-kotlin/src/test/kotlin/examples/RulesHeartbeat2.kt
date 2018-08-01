/*
 * Sponge Knowledge base
 * Heartbeat 2
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

class RulesHeartbeat2 : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("soundTheAlarm", AtomicBoolean(false))
    }

    /** Sounds the alarm when heartbeat event stops occurring at most every 2 seconds. */
    class HeartbeatRule : KRule() {
        override fun onConfigure() {
            setEvents("heartbeat h1", "heartbeat h2 :none")
            duration = Duration.ofSeconds(2)
        }

        override fun onRun(event: Event?) {
            logger.info("Sound the alarm!")
            sponge.getVariable<AtomicBoolean>("soundTheAlarm").set(true)
        }
    }


    override fun onStartup() {
        sponge.event("heartbeat").send()
        sponge.event("heartbeat").sendAfter(1000)
    }
}
