"""
Sponge Knowledge base
Using correlator duration
"""

from org.openksavi.sponge.examples import SampleJavaCorrelator

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
    sponge.setVariable("hardwareFailureJavaFinishCount", AtomicInteger(0))

class SampleCorrelator(Correlator):
    def onConfigure(self):
        self.events = ["filesystemFailure", "diskFailure"]
        self.maxInstances = 1
    def onAcceptAsFirst(self, event):
        return event.name == "filesystemFailure"
    def onInit(self):
        self.eventLog = []
    def onEvent(self, event):
        self.eventLog.append(event)
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, str(self.eventLog))
        sponge.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if len(self.eventLog) == 4:
            sponge.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            self.finish()

def onLoad():
    sponge.enableJava(SampleJavaCorrelator)

def onStartup():
    sponge.event("filesystemFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
    sponge.event("diskFailure").set("source", "server1").send()
    sponge.event("diskFailure").set("source", "server2").send()
