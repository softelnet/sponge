"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("receivedCamelMessage", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.event = "spongeEvent"
    def onRun(self, event):
        camel.send(event.get("message"))
