/**
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    sponge.setVariable("correlationEventsLog", new CorrelationEventsLog())
}

class RuleFFF extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e2", "e3 :first"]).withSynchronous(true)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFFF", this)
    }
}

class RuleFFL extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e2", "e3 :last"]).withDuration(Duration.ofSeconds(2)).withSynchronous(false)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFFL", this)
    }
}

void onStartup() {
    sponge.event("e1").set("label", "1").send()
    sponge.event("e2").set("label", "2").send()
    sponge.event("e2").set("label", "3").send()
    sponge.event("e2").set("label", "4").send()
    sponge.event("e3").set("label", "5").send()
    sponge.event("e3").set("label", "6").send()
    sponge.event("e3").set("label", "7").send()
}
