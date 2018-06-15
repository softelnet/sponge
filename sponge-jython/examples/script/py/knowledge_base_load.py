"""
Sponge Knowledge base
Loading knowledge bases
Note that auto-enable is turned off in the configuration.
"""

from java.util import Collections, HashMap
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    global eventCounter
    # Variables for assertions only
    eventCounter = Collections.synchronizedMap(HashMap())
    eventCounter.put("Trigger1, file1", AtomicInteger(0))
    eventCounter.put("Trigger2, file1", AtomicInteger(0))
    eventCounter.put("Trigger1, file2", AtomicInteger(0))
    eventCounter.put("Trigger2, file2", AtomicInteger(0))
    eventCounter.put("Trigger1, file3", AtomicInteger(0))
    eventCounter.put("Trigger3, file3", AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)

class Trigger1(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger1, file1"
        self.event = "e1"
    def onRun(self, event):
        #self.logger.debug("file1: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()


class Trigger2(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger2, file1"
        self.event = "e2"
    def onRun(self, event):
        #self.logger.debug("file1: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()


class LoadKbFile(Trigger):
    def onConfigure(self):
        self.event = "loadKbFile"
    def onRun(self, event):
        kbFile = event.get("kbFile")
        EPS.kb.load(kbFile)
        self.logger.info("File {} loaded", kbFile)

def onLoad():
    EPS.enableAll(Trigger1, Trigger2, LoadKbFile)

def onStartup():
    EPS.logger.debug("onStartup, file1: {}, triggers: {}", EPS.info, EPS.engine.triggers)
    EPS.event("e1").sendAfter(0, 500)
    EPS.event("e2").sendAfter(0, 500)

    EPS.event("loadKbFile").set("kbFile", "examples/script/py/knowledge_base_load2.py").sendAfter(2000)
    EPS.event("loadKbFile").set("kbFile", "examples/script/py/knowledge_base_load3.py").sendAfter(5000)

def onShutdown():
    EPS.logger.debug("onShutdown, file1")








