"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("calledCustomAction", None)
    EPS.setVariable("sentCamelMessage_spongeProducer", AtomicBoolean(False))

class UnusedCamelTrigger(Trigger):
    def configure(self):
        self.eventName = "spongeProducer"
    def run(self, event):
        EPS.getVariable("sentCamelMessage_" + event.name).set(True)

class CustomAction(Action):
    def run(self, args):
        EPS.setVariable("calledCustomAction", args[0].getIn().body)
        return "OK"
