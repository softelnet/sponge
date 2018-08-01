"""
Sponge Knowledge base
Test - shutdown
"""

from java.util.concurrent import TimeUnit
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    sponge.setVariable("sentEvents", AtomicInteger(100))
    sponge.setVariable("finishedEvents", AtomicInteger(0))

class F1(Filter):
    def onConfigure(self):
        self.event = "e1"
    def onAccept(self, event):
        return True

class T1(Trigger):
    def onConfigure(self):
        self.event = "e1"
    def onRun(self, event):
        sponge.logger.debug("Input event queue: {}, main event queue: {}, decomposed queue: {}, worker thread pool queue: {}",
              sponge.engine.eventQueueManager.inputEventQueue.size,
              sponge.engine.eventQueueManager.mainEventQueue.size, sponge.engine.processingUnitManager.mainProcessingUnit.decomposedQueue.size,
              sponge.engine.processingUnitManager.mainProcessingUnit.workerThreadPool.executor.queue.size())
        TimeUnit.MILLISECONDS.sleep(5000 if event.get("index") == 0 else 100)
        sponge.getVariable("finishedEvents").incrementAndGet()
        self.logger.debug("Finished event {}", event.id)

def onStartup():
    for i in xrange(sponge.getVariable("sentEvents").intValue()):
        sponge.event("e1").set("index", i).send()
