/*
 * Sponge Knowledge base
 * Using rules - events
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.rule.Rule
import org.openksavi.sponge.test.util.CorrelationEventsLog

class RulesNoneModeEventsConditions : KKnowledgeBase() {

    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("correlationEventsLog", correlationEventsLog)
    }

    // Naming F(irst), L(ast), A(ll), N(one)

    class RuleFNF : KRule() {
        override fun onConfigure() {
            withEvents("e1", "e2 :none", "e3").withCondition("e2", this::e2LabelCondition)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(meta.name, this)
        }

        fun e2LabelCondition(event: Event) = event.get<String>("label").toInt() > 4
    }

    class RuleFNNFReject : KRule() {
        override fun onConfigure() {
            withEvents("e1", "e2 :none", "e6 :none", "e3").withCondition("e2", this::e2LabelCondition)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(meta.name, this)
        }

        fun e2LabelCondition(event: Event) = event.get<String>("label").toInt() in 2..4
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
