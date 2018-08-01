"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean
from org.openksavi.sponge.camel import CamelUtils

def onInit():
    # Variables for assertions only
    sponge.setVariable("sentCamelMessage_camelEvent", AtomicBoolean(False))
    sponge.setVariable("sentCamelMessage_spongeProducer", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.event = "camelEvent"
    def onRun(self, event):
        print event.body
        sponge.getVariable("sentCamelMessage_" + event.name).set(True)

class UnusedCamelTrigger(Trigger):
    def onConfigure(self):
        self.event = "spongeProducer"
    def onRun(self, event):
        print event.body
        sponge.getVariable("sentCamelMessage_" + event.name).set(True)

class CamelProducerAction(Action):
    def onCall(self, exchange):
        return sponge.event(camel.makeInputEvent("camelEvent", exchange)).send()
