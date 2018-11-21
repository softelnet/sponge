"""
Sponge Knowledge base
Used for testing a REST API server and clients.
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("actionCalled", AtomicBoolean(False))
    sponge.setVariable("eventSent", AtomicBoolean(False))
    sponge.setVariable("reloaded", AtomicBoolean(False))

def onLoad():
    sponge.kb.version = 1

class UpperCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256)).displayName("Text to upper case").description("The text that will be converted to upper case.")
        ]
        self.resultMeta = ResultMeta(StringType()).displayName("Upper case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        sponge.getVariable("actionCalled").set(True)
        return str(text).upper()

class LowerCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to lower case"
        self.description = "Converts a string to lower case."
        self.argsMeta = [ ArgMeta("text", StringType()).displayName("A text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(StringType()).displayName("Lower case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return str(text).lower()

class EchoImage(Action):
    def onConfigure(self):
        self.displayName = "Echo an image"
        self.argsMeta = [ArgMeta("image", BinaryType().mimeType("image/png")).displayName("Image")]
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/png")).displayName("Image echo")
    def onCall(self, image):
        return image

class ListValues(Action):
    def onConfigure(self):
        self.features = {"visible":False}
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ActionTypeAction(Action):
    def onConfigure(self):
        self.displayName = "Action type use case"
        self.argsMeta = [ArgMeta("value", ActionType("ListValues")).displayName("Value")]
        self.resultMeta = ResultMeta(StringType()).displayName("Same value")
    def onCall(self, value):
        return value

class PrivateAction(Action):
    def onCall(self, args):
        return None

class NoMetadataAction(Action):
    def onCall(self, args):
        return None

class KnowledgeBaseErrorAction(Action):
    def onConfigure(self):
        self.displayName = "Knowledge base error action"
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        raise Exception("Knowledge base exception")

class LangErrorAction(Action):
    def onConfigure(self):
        self.displayName = "Language error action"
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        return throws_error

class ComplexObjectAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject")).displayName("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject")).displayName("Upper case text")
    def onCall(self, arg):
        self.logger.info("Action {} called", self.name)
        arg.id += 1
        return arg

class ComplexObjectListAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ListType(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject"))).displayName("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ListType(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject"))).displayName("Upper case text")
    def onCall(self, arg):
        self.logger.info("Action {} called: {}", self.name, arg)
        arg[0].id += 1
        return arg

class ComplexObjectHierarchyAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("stringArg", StringType()),
            ArgMeta("anyArg", AnyType()),
            ArgMeta("stringListArg", ListType(StringType())),
            ArgMeta("decimalListArg", ListType(ObjectType("java.math.BigDecimal"))),
            ArgMeta("stringArrayArg", ObjectType("java.lang.String[]")),
            ArgMeta("mapArg", MapType(StringType(), ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject")))
        ]
        self.resultMeta = ResultMeta(ListType(AnyType()))
    def onCall(self, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg):
        self.logger.info("Action {} called: {}, {}, {}, {}, {}, {}", self.name, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg)
        return [stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg]

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
        sponge.getVariable("eventSent").set(True)

def onAfterReload():
    sponge.getVariable("reloaded").set(True)
