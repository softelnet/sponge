/*
 * Sponge Knowledge base
 * Using correlator duration
 */

import org.openksavi.sponge.examples.SampleJavaCorrelator
import java.util.concurrent.atomic.AtomicInteger

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaFinishCount", AtomicInteger(0))
}

class SampleCorrelator : Correlator() {
    var eventLog = mutableListOf<Event>()

    override fun onConfigure() {
        setEvents("filesystemFailure", "diskFailure")
        maxInstances = 1
    }

    override fun onAcceptAsFirst(event: Event) = event.name == "filesystemFailure"

    override fun onEvent(event: Event) {
        eventLog.add(event)
        logger.debug("{} - event: {}, log: {}", hashCode(), event.name, eventLog)
        eps.getVariable(AtomicInteger::class, "hardwareFailureScriptCount").incrementAndGet()
        if (eventLog.size == 4) {
            eps.getVariable(AtomicInteger::class, "hardwareFailureScriptFinishCount").incrementAndGet()
            finish()
        }
    }
}

fun onLoad() {
    EPS.enableJava(SampleJavaCorrelator::class.java)
}

fun onStartup() {
    with (EPS) {
        event("filesystemFailure").set("source", "server1").send()
        event("diskFailure").set("source", "server1").send()
        event("diskFailure").set("source", "server2").send()
        event("diskFailure").set("source", "server1").send()
        event("diskFailure").set("source", "server2").send()
    }
}
