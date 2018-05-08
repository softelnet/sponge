"""
Sponge Knowledge base
REST API
"""
from org.openksavi.sponge import SpongeException

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("actionCalled", AtomicBoolean(False))
    EPS.setVariable("eventSent", AtomicBoolean(False))

class UpperCase(Action):
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        EPS.getVariable("actionCalled").set(True)
        return [str(s).upper() for s in args]

class PrivateAction(Action):
    def onCall(self, args):
        return None

class FaultyAction(Action):
    def onCall(self, args):
        raise SpongeException("Error in " + self.name)

class Alarm(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        self.logger.debug("Received event: {}", str(event))
        EPS.getVariable("eventSent").set(True)

