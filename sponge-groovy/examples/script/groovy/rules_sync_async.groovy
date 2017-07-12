/**
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", new CorrelationEventsLog())
}

class RuleFFF extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :first"]
        this.synchronous = true
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFF", this)
    }
}

class RuleFFL extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :last"]
        this.duration = Duration.ofMillis(500)
        this.synchronous = false
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFL", this)
    }
}

void onStartup() {
    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
}
