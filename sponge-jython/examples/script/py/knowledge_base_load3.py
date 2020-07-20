"""
Sponge Knowledge Base
Loading knowledge bases
"""

class Trigger1(Trigger):
    def onConfigure(self):
        self.withLabel("Trigger1, file3").withEvent("e1")
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.meta.label).incrementAndGet()

class Trigger3(Trigger):
    def onConfigure(self):
        self.withLabel("Trigger3, file3").withEvent("e3")
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.meta.label).incrementAndGet()

# Execute immediately while loading
sponge.enableAll(Trigger1, Trigger3)

def onShutdown():
    sponge.logger.debug("onShutdown, file3")
