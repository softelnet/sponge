/*
 * Sponge Knowledge base
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
        eps.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
        eps.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
        eps.setVariable("sameSourceFirstFireCount", AtomicInteger(0))
    }

    class FirstRule : KRule() {
        override fun onConfigure() {
            setEvents("filesystemFailure", "diskFailure")
            setOrdered(false)
            addAllConditions({ rule: Rule, event: Event -> rule.firstEvent.get<Any?>("source") == event.get<Any?>("source") })
            addAllConditions({ rule: Rule, event: Event -> Duration.between(rule.firstEvent.time, event.time).seconds <= 2 })
            duration = Duration.ofSeconds(5)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventSequence)
            eps.getVariable<AtomicInteger>("sameSourceFirstFireCount").incrementAndGet()
            eps.event("alarm").set("source", firstEvent.get("source")).send()
        }
    }

    class SameSourceAllRule : KRule() {
        override fun onConfigure() {
            setEvents("filesystemFailure e1", "diskFailure e2 :all")
            setOrdered(false)
            addCondition("e1", Conditions::severityCondition)
            addConditions("e2", Conditions::severityCondition, Conditions::diskFailureSourceCondition)
            duration = Duration.ofSeconds(5)
        }

        override fun onRun(event: Event?) {
            logger.info("Monitoring log [{}]: Critical failure in {}! Events: {}", event?.time, event?.get("source"),
                    eventSequence)
            eps.getVariable<AtomicInteger>("hardwareFailureScriptCount").incrementAndGet()
        }

        companion object Conditions {
            fun severityCondition(@Suppress("UNUSED_PARAMETER") rule: Rule, event: Event) = event.get<Int>("severity") > 5

            fun diskFailureSourceCondition(rule: Rule, event: Event): Boolean {
                // Both events have to have the same source
                return event.get<String>("source") == rule.firstEvent.get<String>("source") &&
                        Duration.between(rule.firstEvent.time, event.time).seconds <= 4
            }
        }
    }

    class AlarmFilter : KFilter() {
        val deduplication = Deduplication("source")
        override fun onConfigure() = setEvent("alarm")
        override fun onInit() {
            deduplication.cacheBuilder.expireAfterWrite(2, TimeUnit.SECONDS)
        }

        override fun onAccept(event: Event) = deduplication.onAccept(event)
    }

    class Alarm : KTrigger() {
        override fun onConfigure() = setEvent("alarm")
        override fun onRun(event: Event) = logger.debug("Received alarm from {}", event.get<String>("source"))
    }

    override fun onLoad() = eps.enableJava(SameSourceJavaUnorderedRule::class.java)

    override fun onStartup() {
        eps.event("diskFailure").set("severity", 10).set("source", "server1").send()
        eps.event("diskFailure").set("severity", 10).set("source", "server2").send()
        eps.event("diskFailure").set("severity", 8).set("source", "server1").send()
        eps.event("diskFailure").set("severity", 8).set("source", "server1").send()
        eps.event("filesystemFailure").set("severity", 8).set("source", "server1").send()
        eps.event("filesystemFailure").set("severity", 6).set("source", "server1").send()
        eps.event("diskFailure").set("severity", 6).set("source", "server1").send()
    }
}
