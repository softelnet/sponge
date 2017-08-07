"""
Sponge Knowledge base
Loading knowledge bases
"""

class Trigger1(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger1, file2"
        self.event = "e1"
    def onRun(self, event):
        self.logger.debug("file2: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()


class Trigger2(Trigger):
    def onConfigure(self):
        self.displayName = "Trigger2, file2"
        self.event = "e2"
    def onRun(self, event):
        self.logger.debug("file2: Received event {}", event)
        global eventCounter
        eventCounter.get(self.displayName).incrementAndGet()

# Execute immediately while loading
EPS.enableAll(Trigger1, Trigger2)

def onShutdown():
    EPS.logger.debug("onShutdown, file2")







