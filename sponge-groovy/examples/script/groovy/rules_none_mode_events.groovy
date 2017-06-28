/**
 * Sponge Knowledge base
 * Using rules - events
 */

import org.openksavi.sponge.core.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", new CorrelationEventsLog())
}


// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e6 :none", "e3"]
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNF", this)
    }
}

class RuleFNNNL extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNNL", this)
    }
}

class RuleFNNNLReject extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNNLRejected", this)
    }
}

class RuleFNFNL extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNFNL", this)
    }
}

void onStartup() {
    EPS.event("e1").set("label", "1").sendAfter(100)
    EPS.event("e2").set("label", "2").sendAfter(200)
    EPS.event("e2").set("label", "3").sendAfter(300)
    EPS.event("e2").set("label", "4").sendAfter(400)
    EPS.event("e3").set("label", "5").sendAfter(500)
    EPS.event("e3").set("label", "6").sendAfter(600)
    EPS.event("e3").set("label", "7").sendAfter(700)
}
