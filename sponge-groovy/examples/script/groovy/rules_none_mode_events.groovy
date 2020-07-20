/**
 * Sponge Knowledge Base
 * Using rules - events
 */

import org.openksavi.sponge.examples.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    sponge.setVariable("correlationEventsLog", new CorrelationEventsLog())
}


// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNNF extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e5 :none", "e6 :none", "e3"])
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNNF", this)
    }
}

class RuleFNNNL extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2))
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNNNL", this)
    }
}

class RuleFNNNLReject extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2))
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNNNLRejected", this)
    }
}

class RuleFNFNL extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e5 :none", "e2", "e7 :none", "e3 :last"]).withDuration(Duration.ofSeconds(2))
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNFNL", this)
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
