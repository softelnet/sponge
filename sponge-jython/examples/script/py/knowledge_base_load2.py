"""
Sponge Knowledge base
Loading knowledge bases
"""

class Trigger1(Trigger):
    def onConfigure(self):
        self.withLabel("Trigger1, file2").withEvent("e1")
    def onRun(self, event):
        #self.logger.debug("file2: Received event {}", event)
        global eventCounter
        eventCounter.get(self.meta.label).incrementAndGet()


class Trigger2(Trigger):
    def onConfigure(self):
        self.withLabel("Trigger2, file2").withEvent("e2")
    def onRun(self, event):
        #self.logger.debug("file2: Received event {}", event)
        global eventCounter
        eventCounter.get(self.meta.label).incrementAndGet()

# Execute immediately while loading
sponge.enableAll(Trigger1, Trigger2)

def onShutdown():
    sponge.logger.debug("onShutdown, file2")







