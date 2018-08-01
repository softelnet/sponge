/*
 * Sponge Knowledge base
 * Using correlator duration
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KCorrelator
import org.openksavi.sponge.kotlin.KKnowledgeBase
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class CorrelatorsDuration : KKnowledgeBase() {

    override fun onInit() {
        sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    }

    class SampleCorrelator : KCorrelator() {
        var eventLog = mutableListOf<Event>()

        companion object {
            val instanceStarted = AtomicBoolean(false)
        }

        override fun onConfigure() {
            setEvents("filesystemFailure", "diskFailure")
            duration = Duration.ofSeconds(2)
        }

        override fun onAcceptAsFirst(event: Event) = SampleCorrelator.instanceStarted.compareAndSet(false, true)

        override fun onEvent(event: Event) {
            eventLog.add(event)
            sponge.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
        }

        override fun onDuration() {
            logger.debug("{} - log: {}", hashCode(), eventLog)
        }
    }

    override fun onStartup() {
        sponge.event("filesystemFailure").set("source", "server1").send()
        sponge.event("diskFailure").set("source", "server1").sendAfter(200, 100)
        sponge.event("diskFailure").set("source", "server2").sendAfter(200, 100)
    }
}
