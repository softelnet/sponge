"""
Sponge Knowledge base
Using correlator duration
"""

from org.openksavi.sponge.examples import SampleJavaCorrelator

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureScriptFinishCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaFinishCount", AtomicInteger(0))

class SampleCorrelator(Correlator):
    def onConfigure(self):
        self.events = ["filesystemFailure", "diskFailure"]
        self.maxInstances = 1
    def onInit(self):
        self.eventLog = []
    def onAcceptAsFirst(self, event):
        return event.name == "filesystemFailure"
    def onEvent(self, event):
        self.eventLog.append(event)
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, str(self.eventLog))
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if len(self.eventLog) == 4:
            EPS.getVariable("hardwareFailureScriptFinishCount").incrementAndGet()
            self.finish()

def onLoad():
    EPS.enableJava(SampleJavaCorrelator)

def onStartup():
    EPS.event("filesystemFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
    EPS.event("diskFailure").set("source", "server1").send()
    EPS.event("diskFailure").set("source", "server2").send()
