"""
Sponge Knowledge base
REST API
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("actionCalled", AtomicBoolean(False))
    EPS.setVariable("eventSent", AtomicBoolean(False))

class UpperCase(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", StringType().maxLength(256)).displayName("Text to upper case") ]
        self.resultMeta = ResultMeta(StringType()).displayName("Upper case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        EPS.getVariable("actionCalled").set(True)
        return str(text).upper()

class LowerCase(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", StringType()).displayName("A text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(StringType()).displayName("Lower case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return str(text).lower()

class PrivateAction(Action):
    def onCall(self, args):
        return None

class NoMetadataAction(Action):
    def onCall(self, args):
        return None

class FaultyAction(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("text", StringType()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, args):
        raise SpongeException("Error in " + self.name)

class RestApiIsActionPublic(Action):
    def onCall(self, actionAdapter):
        return not (actionAdapter.name.startswith("Private") or actionAdapter.name.startswith("RestApi"))

class RestApiIsEventPublic(Action):
    def onCall(self, eventName):
        return True

class Alarm(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        self.logger.debug("Received event: {}", str(event))
        EPS.getVariable("eventSent").set(True)

