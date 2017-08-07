"""
Sponge Knowledge base
Using correlator duration
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))

class SampleCorrelator(Correlator):
    instanceStarted = AtomicBoolean(False)
    def onConfigure(self):
        self.events = ["filesystemFailure", "diskFailure"]
        self.duration = Duration.ofSeconds(2)
    def onInit(self):
        self.eventLog = []
    def onAcceptAsFirst(self, event):
        return SampleCorrelator.instanceStarted.compareAndSet(False, True)
    def onEvent(self, event):
        self.eventLog.append(event)
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def onDuration(self):
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, str(self.eventLog))

def onStartup():
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
