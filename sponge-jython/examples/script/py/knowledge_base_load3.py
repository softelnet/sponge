"""
Sponge Knowledge base
Loading knowledge bases
"""

class Trigger1(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger1, file3"
        self.event = "e1"
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()

class Trigger3(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger3, file3"
        self.event = "e3"
    def onRun(self, event):
        #self.logger.debug("file3: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()

# Execute immediately while loading
EPS.enableAll(Trigger1, Trigger3)

def onShutdown():
    EPS.logger.debug("onShutdown, file3")
