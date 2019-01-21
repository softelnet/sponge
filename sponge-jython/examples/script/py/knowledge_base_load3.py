"""
Sponge Knowledge base
Loading knowledge bases
"""

class Trigger1(Trigger):
    def onConfigure(self):
        self.label = "Trigger1, file3"
        self.event = "e1"
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.label).incrementAndGet()

class Trigger3(Trigger):
    def onConfigure(self):
        self.label = "Trigger3, file3"
        self.event = "e3"
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.label).incrementAndGet()

# Execute immediately while loading
sponge.enableAll(Trigger1, Trigger3)

def onShutdown():
    sponge.logger.debug("onShutdown, file3")
