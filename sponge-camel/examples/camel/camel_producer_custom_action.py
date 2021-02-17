"""
Sponge Knowledge Base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean
from org.openksavi.sponge.camel import CamelAction

def onInit():
    # Variables for assertions only
    sponge.setVariable("calledCustomAction", None)
    sponge.setVariable("sentCamelMessage_spongeProducer", AtomicBoolean(False))

class UnusedCamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spongeProducer")
    def onRun(self, event):
        sponge.getVariable("sentCamelMessage_" + event.name).set(True)

class CustomAction(Action, CamelAction):
    def onCall(self, exchange):
        sponge.setVariable("calledCustomAction", exchange.getIn().body)
        exchange.message.body = "OK"
