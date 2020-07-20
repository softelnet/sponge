/*
 * Sponge Knowledge Base
 * Using rules - events
 */

import org.openksavi.sponge.examples.util.CorrelationEventsLog

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

class RuleFNNF : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e5 :none", "e6 :none", "e3")
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }
}

class RuleFNNNL : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last").withDuration(Duration.ofSeconds(2))
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }
}

class RuleFNNNLReject : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last").withDuration(Duration.ofSeconds(2))
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }
}

class RuleFNFNL : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e5 :none", "e2", "e7 :none", "e3 :last").withDuration(Duration.ofSeconds(2))
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(meta.name, this)
    }
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
