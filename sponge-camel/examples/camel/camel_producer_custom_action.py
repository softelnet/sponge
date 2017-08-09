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
    def onConfigure(self):
        self.event = "spongeProducer"
    def onRun(self, event):
        EPS.getVariable("sentCamelMessage_" + event.name).set(True)

class CustomAction(Action):
    def onCall(self, args):
        EPS.setVariable("calledCustomAction", args[0].getIn().body)
        return "OK"
