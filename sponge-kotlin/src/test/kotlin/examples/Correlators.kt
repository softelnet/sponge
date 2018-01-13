/*
 * Sponge Knowledge base
 * Using correlator duration
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.SampleJavaCorrelator
import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KCorrelator
import org.openksavi.sponge.kotlin.KKnowledgeBase
import java.util.concurrent.atomic.AtomicInteger

class Correlators : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
        eps.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
        eps.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
        eps.setVariable("hardwareFailureJavaFinishCount", AtomicInteger(0))
    }

    class SampleCorrelator : KCorrelator() {
        var eventLog = mutableListOf<Event>()

        override fun onConfigure() {
            setEvents("filesystemFailure", "diskFailure")
            maxInstances = 1
        }

        override fun onAcceptAsFirst(event: Event) = event.name == "filesystemFailure"

        override fun onEvent(event: Event) {
            eventLog.add(event)
            logger.debug("{} - event: {}, log: {}", hashCode(), event.name, eventLog)
            eps.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
            if (eventLog.size == 4) {
                eps.getVariable(AtomicInteger::class, "hardwareFailureScriptFinishCount").incrementAndGet()
                finish()
            }
        }
    }

    override fun onLoad() {
        eps.enableJava(SampleJavaCorrelator::class.java)
    }

    override fun onStartup() {
        with(eps) {
            event("filesystemFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server2").send()
            event("diskFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server2").send()
        }
    }
}
