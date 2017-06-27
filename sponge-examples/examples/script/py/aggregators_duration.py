"""
Sponge Knowledge base
Using aggregators duration
"""

from org.openksavi.sponge.examples import SampleJavaAggregator

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("hardwareFailureScriptCount", AtomicInteger(0))

class SampleAggregator(Aggregator):
    instanceStarted = AtomicBoolean(False)
    def configure(self):
        self.eventNames = ["filesystemFailure", "diskFailure"]
        self.duration = Duration.ofSeconds(2)
    def init(self):
        self.eventLog = []
    def acceptsAsFirst(self, event):
        return SampleAggregator.instanceStarted.compareAndSet(False, True)
    def onEvent(self, event):
        self.eventLog.append(event)
        EPS.getVariable("hardwareFailureScriptCount").incrementAndGet()
    def onDuration(self):
        self.logger.debug("{} - event: {}, log: {}", self.hashCode(), event.name, str(self.eventLog))

def onStartup():
    EPS.event("filesystemFailure").set("source", "server1").sendAfter(100)
    EPS.event("diskFailure").set("source", "server1").sendAfter(200, 100)
    EPS.event("diskFailure").set("source", "server2").sendAfter(200, 100)