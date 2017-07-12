/**
 * Sponge Knowledge base
 * Using rules - events
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

void onInit() {
    EPS.setVariable("defaultDuration", 10)

    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", new CorrelationEventsLog())
}

// Naming F(irst), L(ast), A(ll), N(one)

class RuleF extends Rule {
    void configure() {
        this.events = ["e1"]
    }
    void run(Event event) {
        EPS.getVariable("correlationEventsLog").addEvents("RuleF", this)
    }
}

// F(irst)F(irst)F(irst)
class RuleFFF extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :first"]
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFF", this)
    }
}

class RuleFFFDuration extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :first"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFFDuration", this)
    }
}

// F(irst)F(irst)L(ast)
class RuleFFL extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :last"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}", event.name)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFL", this)
    }
}

// F(irst)F(irst)A(ll)
class RuleFFA extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e3 :all"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFA", this)
    }
}

// F(irst)F(irst)N(one)
class RuleFFN extends Rule {
    void configure() {
        this.events = ["e1", "e2", "e4 :none"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFFN", this)
    }
}

// F(irst)L(ast)F(irst)
class RuleFLF extends Rule {
    void configure() {
        this.events = ["e1", "e2 :last", "e3 :first"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFLF", this)
    }
}

// F(irst)L(ast)L(ast)
class RuleFLL extends Rule {
    void configure() {
        this.events = ["e1", "e2 :last", "e3 :last"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFLL", this)
    }
}

// F(irst)L(ast)A(ll)
class RuleFLA extends Rule {
    void configure() {
        this.events = ["e1", "e2 :last", "e3 :all"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFLA", this)
    }
}

// F(irst)L(ast)N(one)
class RuleFLN extends Rule {
    void configure() {
        this.events = ["e1", "e2 :last", "e4 :none"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFLN", this)
    }
}

// F(irst)A(ll)F(irst)
class RuleFAF extends Rule {
    void configure() {
        this.events = ["e1", "e2 :all", "e3 :first"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFAF", this)
    }
}

// F(irst)A(ll)L(ast)
class RuleFAL extends Rule {
    void configure() {
        this.events = ["e1", "e2 :all", "e3 :last"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFAL", this)
    }
}

// F(irst)A(ll)A(ll)
class RuleFAA extends Rule {
    void configure() {
        this.events = ["e1", "e2 :all", "e3 :all"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for event: {}, sequence: {}", event.name, this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFAA", this)
    }
}

// F(irst)A(ll)N(one)
class RuleFAN extends Rule {
    void configure() {
        this.events = ["e1", "e2 :all", "e5 :none"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFAN", this)
    }
}

// F(irst)N(one)F(irst)
class RuleFNF extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e3"]
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNF", this)
    }
}

// F(irst)N(one)L(ast)
class RuleFNL extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e3 :last"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNL", this)
    }
}

// F(irst)N(one)A(ll)
class RuleFNA extends Rule {
    void configure() {
        this.events = ["e1", "e5 :none", "e3 :all"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNA", this)
    }
}

class RuleFNFReject extends Rule {
    void configure() {
        this.events = ["e1", "e2 :none", "e3"]
        this.duration = Duration.ofMillis(EPS.getVariable("defaultDuration"))
    }
    void run(Event event) {
        this.logger.debug("Running rule for sequence: {}", this.eventSequence)
        EPS.getVariable("correlationEventsLog").addEvents("RuleFNFReject", this)
    }
}

void onStartup() {
    EPS.event("e1").set("label", "0").sendAfter(0, 10)  // Not used in assertions, "background noise" events.
    EPS.event("e1").set("label", "-1").sendAfter(0, 10)
    EPS.event("e1").set("label", "-2").sendAfter(0, 10)
    EPS.event("e1").set("label", "-3").sendAfter(0, 10)

    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
}
