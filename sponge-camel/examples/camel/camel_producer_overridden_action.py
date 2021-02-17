"""
Sponge Knowledge Base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean
from org.openksavi.sponge.camel import CamelAction, CamelUtils

def onInit():
    # Variables for assertions only
    sponge.setVariable("sentCamelMessage_camelEvent", AtomicBoolean(False))
    sponge.setVariable("sentCamelMessage_spongeProducer", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("camelEvent")
    def onRun(self, event):
        print event.body
        sponge.getVariable("sentCamelMessage_" + event.name).set(True)

class UnusedCamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spongeProducer")
    def onRun(self, event):
        print event.body
        sponge.getVariable("sentCamelMessage_" + event.name).set(True)

class CamelProducerAction(Action, CamelAction):
    def onCall(self, exchange):
        event = sponge.event(camel.makeInputEvent("camelEvent", exchange)).send()
        exchange.message.body = event
