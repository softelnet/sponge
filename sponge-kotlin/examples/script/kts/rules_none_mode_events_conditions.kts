/*
 * Sponge Knowledge base
 * Using rules - events
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

class Constants {
    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }
}

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", Constants.correlationEventsLog)
}

// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e2 :none", "e3")
        addConditions("e2", this::e2LabelCondition)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }

    fun e2LabelCondition(@Suppress("UNUSED_PARAMETER") rule: Rule, event: Event) = event.get<String>("label").toInt() > 4
}

class RuleFNNFReject : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e2 :none", "e6 :none", "e3")
        addConditions("e2", this::e2LabelCondition)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }

    fun e2LabelCondition(@Suppress("UNUSED_PARAMETER") rule: Rule, event: Event) = event.get<String>("label").toInt() in 2..4
}

fun onStartup() {
    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
}
