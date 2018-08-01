/**
 * Sponge Knowledge base
 * Using rules - events
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

void onInit() {
    // Variables for assertions only
    sponge.setVariable("correlationEventsLog", new CorrelationEventsLog())
}

// Naming F(irst), L(ast), A(ll), N(one)

class RuleFNF extends Rule {
    void onConfigure() {
        this.events = ["e1", "e2 :none", "e3"]
        this.addConditions("e2", { rule, event -> (event.get("label") as int) > 4 })
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNF", this)
    }
}

class RuleFNNFReject extends Rule {
    void onConfigure() {
        this.events = ["e1", "e2 :none", "e6 :none", "e3"]
        // this.duration = Duration.ofSeconds(2)
        this.addConditions("e2", this.&e2LabelCondition)
    }
    void onRun(Event event) {
        this.logger.debug("Running rule for events: {}", this.eventAliasMap)
        sponge.getVariable("correlationEventsLog").addEvents("RuleFNNFReject", this)
    }
    boolean e2LabelCondition(event) {
        int label = (event.get("label") as int)
        return 2 <= label && label <= 4
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
