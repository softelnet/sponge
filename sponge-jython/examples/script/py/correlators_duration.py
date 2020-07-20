"""
Sponge Knowledge Base
Using correlator duration
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))

class SampleCorrelator(Correlator):
    instanceStarted = AtomicBoolean(False)
    def onConfigure(self):
        self.withEvents(["filesystemFailure", "diskFailure"]).withDuration(Duration.ofSeconds(2))
    def onAcceptAsFirst(self, event):
        return SampleCorrelator.instanceStarted.compareAndSet(False, True)
    def onInit(self):
        self.eventLog = []
    def onEvent(self, event):
        self.eventLog.append(event)
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def onDuration(self):
        self.logger.debug("{} - log: {}", self.hashCode(), str(self.eventLog))

def onStartup():
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    sponge.event("diskFailure").set("source", "server2").sendAfter(200, 100)
