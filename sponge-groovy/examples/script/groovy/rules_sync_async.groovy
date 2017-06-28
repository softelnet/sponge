/**
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

import org.openksavi.sponge.core.util.CorrelationEventsLog

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
    EPS.event("e1").set("label", "1").sendAfter(1)
    EPS.event("e2").set("label", "2").sendAfter(2)
    EPS.event("e2").set("label", "3").sendAfter(3)
    EPS.event("e2").set("label", "4").sendAfter(4)
    EPS.event("e3").set("label", "5").sendAfter(5)
    EPS.event("e3").set("label", "6").sendAfter(6)
    EPS.event("e3").set("label", "7").sendAfter(7)
}
