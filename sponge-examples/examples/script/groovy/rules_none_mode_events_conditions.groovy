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

class RuleFNF extends Rule {
    void configure() {
        this.events = ["e1", "e2 :none", "e3"]
        this.setConditions("e2", { self, event -> (event.get("label") as int) > 4 })
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNF", this)
    }
}

class RuleFNNFReject extends Rule {
    void configure() {
        this.events = ["e1", "e2 :none", "e6 :none", "e3"]
        // this.duration = Duration.ofSeconds(2)
        this.setConditions("e2", this.&e2LabelCondition)
    }
    void run(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNNFReject", this)
    }
    boolean e2LabelCondition(event) {
        int label = (event.get("label") as int)
        return 2 <= label && label <= 4
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
