/**
 * Sponge Knowledge base
 * Using rules - events
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", new CorrelationEventsLog())
}


// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF extends Rule {
    void onConfigure() {
        this.events = ["e1", "e5 :none", "e6 :none", "e3"]
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNF", this)
    }
}

class RuleFNNNL extends Rule {
    void onConfigure() {
        this.events = ["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNNL", this)
    }
}

class RuleFNNNLReject extends Rule {
    void onConfigure() {
        this.events = ["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNNLRejected", this)
    }
}

class RuleFNFNL extends Rule {
    void onConfigure() {
        this.events = ["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]
        this.duration = Duration.ofSeconds(2)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNFNL", this)
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
