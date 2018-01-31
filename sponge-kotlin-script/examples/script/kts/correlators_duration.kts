/*
 * Sponge Knowledge base
 * Using correlator duration
 */

import org.openksavi.sponge.event.Event
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
}

class SampleCorrelator : Correlator() {
    var eventLog = mutableListOf<Event>()

    companion object {
        val instanceStarted = AtomicBoolean(false)
    }

    override fun onConfigure() {
        setEvents("filesystemFailure", "diskFailure")
        duration = Duration.ofSeconds(2)
    }

    override fun onAcceptAsFirst(event: Event) = SampleCorrelator.instanceStarted.compareAndSet(false, true)

    override fun onEvent(event: Event) {
        eventLog.add(event)
        eps.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
    }

    override fun onDuration() {
        logger.debug("{} - log: {}", hashCode(), eventLog)
    }
}

fun onStartup() {
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
}
