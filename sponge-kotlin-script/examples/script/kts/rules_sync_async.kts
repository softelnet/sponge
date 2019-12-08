/*
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
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

class RuleFFF : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e2", "e3 :first").withSynchronous(true)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for event: {}", event?.name)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

class RuleFFL : Rule() {
    override fun onConfigure() {
        withEvents("e1", "e2", "e3 :last").withDuration(Duration.ofSeconds(2)).withSynchronous(false)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for event: {}", event?.name)
        Constants.correlationEventsLog.addEvents(name, this)
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
