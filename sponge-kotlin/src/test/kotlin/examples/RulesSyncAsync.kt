/*
 * Sponge Knowledge Base
 * Using rules - synchronous and asynchronous
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.examples.util.CorrelationEventsLog
import java.time.Duration

class RulesSyncAsync : KKnowledgeBase() {

    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("correlationEventsLog", correlationEventsLog)
    }

    class RuleFFF : KRule() {
        override fun onConfigure() {
            withEvents("e1", "e2", "e3 :first").withSynchronous(true)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}", event?.name)
            correlationEventsLog.addEvents(meta.name, this)
        }
    }

    class RuleFFL : KRule() {
        override fun onConfigure() {
            withEvents("e1", "e2", "e3 :last").withDuration(Duration.ofSeconds(2)).withSynchronous(false)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}", event?.name)
            correlationEventsLog.addEvents(meta.name, this)
        }
    }

    override fun onStartup() {
        sponge.event("e1").set("label", "1").send()
        sponge.event("e2").set("label", "2").send()
        sponge.event("e2").set("label", "3").send()
        sponge.event("e2").set("label", "4").send()
        sponge.event("e3").set("label", "5").send()
        sponge.event("e3").set("label", "6").send()
        sponge.event("e3").set("label", "7").send()
    }
}
