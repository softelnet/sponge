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

class RuleFNNF : Rule() {
    override fun onConfigure() = setEvents("e1", "e5 :none", "e6 :none", "e3")

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

class RuleFNNNL : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last")
        duration = Duration.ofSeconds(2)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

class RuleFNNNLReject : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last")
        duration = Duration.ofSeconds(2)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

class RuleFNFNL : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e5 :none", "e2", "e7 :none", "e3 :last")
        duration = Duration.ofSeconds(2)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for events: {}", eventAliasMap)
        Constants.correlationEventsLog.addEvents(name, this)
    }
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
