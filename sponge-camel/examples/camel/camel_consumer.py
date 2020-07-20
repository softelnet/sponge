"""
Sponge Knowledge Base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedCamelMessage", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spongeEvent")
    def onRun(self, event):
        camel.emit(event.get("message"))
