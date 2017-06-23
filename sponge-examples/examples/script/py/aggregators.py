"""
Sponge Knowledge base
Using aggregators duration
"""

from org.openksavi.sponge.examples import SampleJavaAggregator

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))
    EPS.setVariable("hardwareFailureJavaCount", AtomicInteger(0))

class SampleAggregator(Aggregator):
    instanceStarted = AtomicBoolean(False)
    def configure(self):
        self.eventNames = ["filesystemFailure", "diskFailure"]
    def init(self):
        self.eventLog = []
    def acceptsAsFirst(self, event):
        return SampleAggregator.instanceStarted.compareAndSet(False, True)
    def onEvent(self, event):
        self.eventLog.append(event)
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, str(self.eventLog))
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
        if len(self.eventLog) >= 4:
            self.finish()

def onLoad():
    EPS.enableJava(SampleJavaAggregator)

def onStartup():
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)
