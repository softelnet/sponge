/*
 * Sponge Knowledge base
 * Using rules - events
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.core.util.SpongeUtils
import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.SameSourceJavaRule
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.test.util.CorrelationEventsLog
import java.time.Duration

class RulesEvents : KKnowledgeBase() {

    companion object {
        val correlationEventsLog = CorrelationEventsLog()
        val defaultDuration = 1000L
    }

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("correlationEventsLog", correlationEventsLog)
    }

    // Naming F(irst), L(ast), A(ll), N(one)

    class RuleF : KRule() {
        override fun onConfigure() = setEvents("e1")
        override fun onRun(event: Event?) {
            //eps.getVariable<CorrelationEventsLog>("correlationEventsLog").addEvents(name, this)
            correlationEventsLog.addEvents(name, this)
        }
    }

    // F(irst)F(irst)F(irst)
    class RuleFFF : KRule() {
        override fun onConfigure() = setEvents("e1", "e2", "e3 :first")
        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}", event?.name)
            correlationEventsLog.addEvents(name, this)
        }
    }

    abstract class TestRule : KRule() {
        fun setup(vararg eventSpec: String) {
            setEvents(eventSpec)
            duration = Duration.ofMillis(defaultDuration)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for event: {}, sequence: {}", event?.name, SpongeUtils.toStringEventSequence(eventSequence, "label"))
            correlationEventsLog.addEvents(name, this)
        }
    }

    class RuleFFFDuration : TestRule() {
        override fun onConfigure() = setup("e1", "e2", "e3 :first")
    }

    // F(irst)F(irst)L(ast)
    class RuleFFL : TestRule() {
        override fun onConfigure() = setup("e1", "e2", "e3 :last")
    }

    // F(irst)F(irst)A(ll)
    class RuleFFA : TestRule() {
        override fun onConfigure() = setup("e1", "e2", "e3 :all")
    }

    // F(irst)F(irst)N(one)
    class RuleFFN : TestRule() {
        override fun onConfigure() = setup("e1", "e2", "e4 :none")
    }

    // F(irst)L(ast)F(irst)
    class RuleFLF : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :last", "e3 :first")
    }

    // F(irst)L(ast)L(ast)
    class RuleFLL : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :last", "e3 :last")
    }

    // F(irst)L(ast)A(ll)
    class RuleFLA : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :last", "e3 :all")
    }

    // F(irst)L(ast)N(one)
    class RuleFLN : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :last", "e4 :none")
    }

    // F(irst)A(ll)F(irst)
    class RuleFAF : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :all", "e3 :first")
    }

    // F(irst)A(ll)L(ast)
    class RuleFAL : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :all", "e3 :last")
    }

    // F(irst)A(ll)A(ll)
    class RuleFAA : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :all", "e3 :all")
    }

    // F(irst)A(ll)N(one)
    class RuleFAN : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :all", "e5 :none")
    }

    // F(irst)N(one)F(irst)
    class RuleFNF : TestRule() {
        override fun onConfigure() = setup("e1", "e5 :none", "e3")
    }

    // F(irst)N(one)L(ast)
    class RuleFNL : TestRule() {
        override fun onConfigure() = setup("e1", "e5 :none", "e3 :last")
    }

    // F(irst)N(one)A(ll)
    class RuleFNA : TestRule() {
        override fun onConfigure() = setup("e1", "e5 :none", "e3 :all")
    }

    class RuleFNFReject : TestRule() {
        override fun onConfigure() = setup("e1", "e2 :none", "e3")
    }

    override fun onStartup() {
        eps.event("e1").set("label", "0").sendAfter(0, 200)  // Not used in assertions, "background noise" events.
        eps.event("e1").set("label", "-1").sendAfter(0, 200)
        eps.event("e1").set("label", "-2").sendAfter(0, 200)
        eps.event("e1").set("label", "-3").sendAfter(0, 200)

        eps.event("e1").set("label", "1").send()
        eps.event("e2").set("label", "2").send()
        eps.event("e2").set("label", "3").send()
        eps.event("e2").set("label", "4").send()
        eps.event("e3").set("label", "5").send()
        eps.event("e3").set("label", "6").send()
        eps.event("e3").set("label", "7").send()
    }
}
