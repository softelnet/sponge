"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean
from org.openksavi.sponge.camel import CamelUtils

def onInit():
    # Variables for assertions only
    EPS.setVariable("sentCamelMessage_camelEvent", AtomicBoolean(False))
    EPS.setVariable("sentCamelMessage_spongeProducer", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def configure(self):
        self.event = "camelEvent"
    def run(self, event):
        print event.body
        EPS.getVariable("sentCamelMessage_" + event.name).set(True)

class UnusedCamelTrigger(Trigger):
    def configure(self):
        self.event = "spongeProducer"
    def run(self, event):
        print event.body
        EPS.getVariable("sentCamelMessage_" + event.name).set(True)

class CamelProducerAction(Action):
    def run(self, args):
        return EPS.event(camel.makeInputEvent("camelEvent", args[0])).send()
