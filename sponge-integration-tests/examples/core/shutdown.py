"""
Sponge Knowledge base
Test - shutdown
"""

from java.util.concurrent import TimeUnit
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    EPS.setVariable("sentEvents", AtomicInteger(100))
    EPS.setVariable("finishedEvents", AtomicInteger(0))

class F1(Filter):
    def configure(self):
        self.event = "e1"
    def accepts(self, event):
        return True

class T1(Trigger):
    def configure(self):
        self.event = "e1"
    def run(self, event):
        EPS.logger.debug("Input event queue: {}, main event queue: {}, decomposed queue: {}, worker thread pool queue: {}",
              EPS.engine.eventQueueManager.inputEventQueue.size,
              EPS.engine.eventQueueManager.mainEventQueue.size, EPS.engine.processingUnitManager.mainProcessingUnit.decomposedQueue.size,
              EPS.engine.processingUnitManager.mainProcessingUnit.workerThreadPool.executor.queue.size())
        TimeUnit.MILLISECONDS.sleep(5000 if event.get("index") == 0 else 100)
        EPS.getVariable("finishedEvents").incrementAndGet()
        self.logger.debug("Finished event {}", event.id)

def onStartup():
    for i in xrange(EPS.getVariable("sentEvents").intValue()):
        EPS.event("e1").set("index", i).send()
