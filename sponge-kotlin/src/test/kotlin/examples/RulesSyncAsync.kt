/*
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.test.util.CorrelationEventsLog
import java.time.Duration

class RulesSyncAsync : KKnowledgeBase() {

    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("correlationEventsLog", correlationEventsLog)
    }

    class RuleFFF : KRule() {
        override fun onConfigure() {
            setEvents("e1", "e2", "e3 :first")
            setSynchronous(true)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}", event?.name)
            correlationEventsLog.addEvents(name, this)
        }
    }

    class RuleFFL : KRule() {
        override fun onConfigure() {
            setEvents("e1", "e2", "e3 :last")
            duration = Duration.ofSeconds(2)
            setSynchronous(false)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}", event?.name)
            correlationEventsLog.addEvents(name, this)
        }
    }

    override fun onStartup() {
        eps.event("e1").set("label", "1").send()
        eps.event("e2").set("label", "2").send()
        eps.event("e2").set("label", "3").send()
        eps.event("e2").set("label", "4").send()
        eps.event("e3").set("label", "5").send()
        eps.event("e3").set("label", "6").send()
        eps.event("e3").set("label", "7").send()
    }
}
