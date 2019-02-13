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
        sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
        sponge.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
        sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
        sponge.setVariable("hardwareFailureJavaFinishCount", AtomicInteger(0))
    }

    class SampleCorrelator : KCorrelator() {
        var eventLog = mutableListOf<Event>()

        override fun onConfigure() {
            withEvents("filesystemFailure", "diskFailure").withMaxInstances(1)
        }

        override fun onAcceptAsFirst(event: Event) = event.name == "filesystemFailure"

        override fun onEvent(event: Event) {
            eventLog.add(event)
            logger.debug("{} - event: {}, log: {}", hashCode(), event.name, eventLog)
            sponge.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
            if (eventLog.size == 4) {
                sponge.getVariable(AtomicInteger::class, "hardwareFailureScriptFinishCount").incrementAndGet()
                finish()
            }
        }
    }

    override fun onLoad() {
        sponge.enableJava(SampleJavaCorrelator::class.java)
    }

    override fun onStartup() {
        with(sponge) {
            event("filesystemFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server2").send()
            event("diskFailure").set("source", "server1").send()
            event("diskFailure").set("source", "server2").send()
        }
    }
}
