/*
 * Sponge Knowledge Base
 * Correlator builders
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KCorrelator
import org.openksavi.sponge.kotlin.KCorrelatorBuilder
import org.openksavi.sponge.kotlin.KKnowledgeBase
import java.util.concurrent.atomic.AtomicInteger
import java.util.Collections

class CorrelatorsBuilder : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
        sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
        sponge.setVariable("eventLogs", Collections.synchronizedMap(mutableMapOf<String, MutableList<Event>>()))
    }

    override fun onLoad() {
        sponge.enable(KCorrelatorBuilder("SampleCorrelator").withEvents("filesystemFailure", "diskFailure").withMaxInstances(1)
                .withOnAcceptAsFirst({ correlator, event -> event.name == "filesystemFailure"})
                .withOnInit({ correlator -> sponge.getVariable<MutableMap<String, MutableList<Event>>>("eventLogs")[correlator.meta.name] = mutableListOf<Event>() })
                .withOnEvent({ correlator, event ->
                    val eventLog = sponge.getVariable<MutableMap<String, MutableList<Event>>>("eventLogs").get(correlator.meta.name)

                    eventLog!!.add(event)
                    logger.debug("{} - event: {}, log: {}", hashCode(), event.name, eventLog)
                    sponge.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
                    if (eventLog.size == 4) {
                        sponge.getVariable(AtomicInteger::class, "hardwareFailureScriptFinishCount").incrementAndGet()
                        correlator.finish()
                    }
                }))
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
