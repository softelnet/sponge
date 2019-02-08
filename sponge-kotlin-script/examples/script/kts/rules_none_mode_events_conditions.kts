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
    sponge.setVariable("correlationEventsLog", Constants.correlationEventsLog)
}

// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e2 :none", "e3").withCondition("e2", this::e2LabelCondition)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }

    fun e2LabelCondition(event: Event) = event.get<String>("label").toInt() > 4
}

class RuleFNNFReject : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e2 :none", "e6 :none", "e3").withCondition("e2", this::e2LabelCondition)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }

    fun e2LabelCondition(event: Event) = event.get<String>("label").toInt() in 2..4
}

fun onStartup() {
    sponge.event("e1").set("label", "1").send()
    sponge.event("e2").set("label", "2").send()
    sponge.event("e2").set("label", "3").send()
    sponge.event("e2").set("label", "4").send()
    sponge.event("e3").set("label", "5").send()
    sponge.event("e3").set("label", "6").send()
    sponge.event("e3").set("label", "7").send()
}
