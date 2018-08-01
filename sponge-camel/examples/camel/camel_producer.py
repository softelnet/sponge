"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("sentCamelMessage", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.event = "spongeProducer"
    def onRun(self, event):
        print event.body
        sponge.getVariable("sentCamelMessage").set(True)
