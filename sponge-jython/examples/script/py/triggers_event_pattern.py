"""
Sponge Knowledge base
Triggers - Event pattern
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("countA", AtomicInteger(0))
    sponge.setVariable("countAPattern", AtomicInteger(0))

class TriggerA(Trigger):
    def onConfigure(self):
        self.withEvent("a")
    def onRun(self, event):
        sponge.getVariable("countA").incrementAndGet()

class TriggerAPattern(Trigger):
    def onConfigure(self):
        self.withEvent("a.*")
    def onRun(self, event):
        self.logger.debug("Received matching event {}", event.name)
        sponge.getVariable("countAPattern").incrementAndGet()

def onStartup():
    for name in ["a", "a1", "a2", "aTest", "b1", "b2", "bTest", "a", "A", "A1" ]:
        sponge.event(name).send()
