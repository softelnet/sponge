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
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", Type.STRING, True, "Text to upper case") ]
        self.resultMeta = ResultMeta(Type.STRING, "Upper case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        EPS.getVariable("actionCalled").set(True)
        return str(args[0]).upper()

class LowerCase(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", Type.STRING, True, "A text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(Type.STRING, "Lower case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        return str(args[0]).lower()

class PrivateAction(Action):
    def onCall(self, args):
        return None

class NoMetadataAction(Action):
    def onCall(self, args):
        return None

class FaultyAction(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", Type.STRING, True) ]
        self.resultMeta = ResultMeta(Type.VOID)
    def onCall(self, args):
        raise SpongeException("Error in " + self.name)

class RestApiIsActionPublic(Action):
    def onCall(self, args):
        actionAdapter = args[0]
        return not (actionAdapter.name.startswith("Private") or actionAdapter.name.startswith("RestApi"))

class RestApiIsEventPublic(Action):
    def onCall(self, args):
        eventName = args[0]
        return True

class Alarm(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        self.logger.debug("Received event: {}", str(event))
        EPS.getVariable("eventSent").set(True)
