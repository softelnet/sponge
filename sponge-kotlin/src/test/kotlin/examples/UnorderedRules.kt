/*
 * Sponge Knowledge Base
 * Using unordered rules
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.core.library.Deduplication
import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.SameSourceJavaUnorderedRule
import org.openksavi.sponge.kotlin.KFilter
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.kotlin.KTrigger
import org.openksavi.sponge.rule.Rule
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class UnorderedRules : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
        sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
        sponge.setVariable("sameSourceFirstFireCount", AtomicInteger(0))
    }

    class FirstRule : KRule() {
        override fun onConfigure() {
            withEvents("filesystemFailure", "diskFailure").withOrdered(false)
            withAllConditions(
                { rule: Rule, event: Event -> rule.firstEvent.get<Any?>("source") == event.get<Any?>("source") },
                { rule: Rule, event: Event -> Duration.between(rule.firstEvent.time, event.time).seconds <= 2 }
            )
            withDuration(Duration.ofSeconds(5))
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventSequence)
            sponge.getVariable<AtomicInteger>("sameSourceFirstFireCount").incrementAndGet()
            sponge.event("alarm").set("source", firstEvent.get("source")).send()
        }
    }

    class SameSourceAllRule : KRule() {
        override fun onConfigure() {
            withEvents("filesystemFailure e1", "diskFailure e2 :all").withOrdered(false)
            withCondition("e1", this::severityCondition)
            withConditions("e2", this::severityCondition, this::diskFailureSourceCondition)
            withDuration(Duration.ofSeconds(5))
        }

        override fun onRun(event: Event?) {
            logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event?.time, event?.get("source"),
                    eventSequence)
            sponge.getVariable<AtomicInteger>("hardwareFailureScriptCount").incrementAndGet()
        }

        fun severityCondition(event: Event) = event.get<Int>("severity") > 5

        fun diskFailureSourceCondition(event: Event): Boolean {
            // Both events have to have the same source
            return event.get<String>("source") == this.firstEvent.get<String>("source") &&
                    Duration.between(this.firstEvent.time, event.time).seconds <= 4
        }
    }

    class AlarmFilter : KFilter() {
        val deduplication = Deduplication("source")
        override fun onConfigure() {
            withEvent("alarm")
        }
        override fun onInit() {
            deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit.SECONDS)
        }

        override fun onAccept(event: Event) = deduplication.onAccept(event)
    }

    class Alarm : KTrigger() {
        override fun onConfigure() {
            withEvent("alarm")
        }
        override fun onRun(event: Event) = logger.debug("Received alarm from {}", event.get<String>("source"))
    }

    override fun onLoad() = sponge.enableJava(SameSourceJavaUnorderedRule::class.java)

    override fun onStartup() {
        sponge.event("diskFailure").set("severity", 10).set("source", "server1").send()
        sponge.event("diskFailure").set("severity", 10).set("source", "server2").send()
        sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
        sponge.event("diskFailure").set("severity", 8).set("source", "server1").send()
        sponge.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
        sponge.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
        sponge.event("diskFailure").set("severity", 6).set("source", "server1").send()
    }
}
