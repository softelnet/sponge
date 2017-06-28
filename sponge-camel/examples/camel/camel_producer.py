"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("sentCamelMessage", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def configure(self):
        self.eventName = "spongeProducer"
    def run(self, event):
        print event.body
        EPS.getVariable("sentCamelMessage").set(True)
