"""
Sponge Knowledge base
Used for testing a REST API server and clients.
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only.
    sponge.setVariable("actionCalled", AtomicBoolean(False))
    sponge.setVariable("eventSent", AtomicBoolean(False))
    sponge.setVariable("reloaded", AtomicBoolean(False))

    # Variables that simulate a data model.
    sponge.setVariable("actuator1", "A")
    sponge.setVariable("actuator2", False)
    sponge.setVariable("actuator3", 1)
    sponge.setVariable("actuator4", 1)
    sponge.setVariable("actuator5", "X")

    sponge.addCategories(CategoryMeta("category1").label("Category 1").description("Category 1 description"),
                         CategoryMeta("category2").label("Category 2").description("Category 2 description"))
def onLoad():
    sponge.kb.version = 2

class UpperCase(Action):
    def onConfigure(self):
        self.label = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.category = "category1"
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256)).label("Text to upper case").description("The text that will be converted to upper case.")
        ]
        self.resultMeta = ResultMeta(StringType()).label("Upper case text")
        self.version = 2
    def onCall(self, text):
        self.logger.info("Action {} called: {}", self.name, text)
        sponge.getVariable("actionCalled").set(True)
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.label = "Convert to lower case"
        self.description = "Converts a string to lower case."
        self.category = "category1"
        self.argsMeta = [ ArgMeta("text", StringType()).label("A text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(StringType()).label("Lower case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return text.lower()

class EchoImage(Action):
    def onConfigure(self):
        self.label = "Echo an image"
        self.category = "category2"
        self.argsMeta = [ArgMeta("image", BinaryType().mimeType("image/png")).label("Image")]
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/png")).label("Image echo")
    def onCall(self, image):
        return image

class ListValues(Action):
    def onConfigure(self):
        self.features = {"visible":False}
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.label = "Action with provided argument"
        self.category = "category2"
        self.argsMeta = [ArgMeta("value", StringType()).label("Value").provided(ArgProvidedMeta().valueSet())]
        self.resultMeta = ResultMeta(StringType()).label("Same value")
    def onCall(self, value):
        return value
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValueSet(sponge.call("ListValues"))

class PrivateAction(Action):
    def onCall(self, args):
        return None

class NoMetadataAction(Action):
    def onCall(self, args):
        return None

class KnowledgeBaseErrorAction(Action):
    def onConfigure(self):
        self.label = "Knowledge base error action"
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        raise Exception("Knowledge base exception")

class LangErrorAction(Action):
    def onConfigure(self):
        self.label = "Language error action"
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        return throws_error

class ComplexObjectAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject")).label("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject")).label("Upper case text")
    def onCall(self, arg):
        self.logger.info("Action {} called", self.name)
        arg.id += 1
        return arg

class ComplexObjectListAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ListType(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject"))).label("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ListType(ObjectType("org.openksavi.sponge.restapi.test.base.CompoundComplexObject"))).label("Upper case text")
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

class SetActuator(Action):
    def onConfigure(self):
        self.label = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).label("Actuator 1 state").provided(ArgProvidedMeta().value().valueSet()),
            ArgMeta("actuator2", BooleanType()).label("Actuator 2 state").provided(ArgProvidedMeta().value()),
            ArgMeta("actuator3", IntegerType().nullable()).label("Actuator 3 state").provided(ArgProvidedMeta().value().readOnly()),
            ArgMeta("actuator4", IntegerType()).label("Actuator 4 state")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in names:
            provided["actuator2"] = ArgProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgProvidedValue().withValue(sponge.getVariable("actuator3", None))

class SetActuatorDepends(Action):
    def onConfigure(self):
        self.label = "Set actuator with depends"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).label("Actuator 1 state").provided(ArgProvidedMeta().value().valueSet()),
            ArgMeta("actuator2", BooleanType()).label("Actuator 2 state").provided(ArgProvidedMeta().value()),
            ArgMeta("actuator3", IntegerType()).label("Actuator 3 state").provided(ArgProvidedMeta().value()),
            ArgMeta("actuator4", IntegerType()).label("Actuator 4 state"),
            ArgMeta("actuator5", StringType()).label("Actuator 5 state").provided(ArgProvidedMeta().value().valueSet().depends("actuator1")),
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        sponge.setVariable("actuator3", actuator3)
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgProvidedValue().withValue(sponge.getVariable("actuator1", None)).withAnnotatedValueSet(
                [AnnotatedValue("A").withLabel("Value A"), AnnotatedValue("B").withLabel("Value B"), AnnotatedValue("C").withLabel("Value C")])
        if "actuator2" in names:
            provided["actuator2"] = ArgProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgProvidedValue().withValue(sponge.getVariable("actuator3", None))
        if "actuator5" in names:
            provided["actuator5"] = ArgProvidedValue().withValue(sponge.getVariable("actuator5", None)).withValueSet(["X", "Y", "Z", current["actuator1"]])

class AnnotatedTypeAction(Action):
    def onConfigure(self):
        self.argsMeta = [ArgMeta("arg1", AnnotatedType(BooleanType())).label("Argument 1")]
        self.resultMeta = ResultMeta(AnnotatedType(StringType())).label("Annotated result")
    def onCall(self, arg1):
        features = {"feature1":"value1"}
        features.update(arg1.features)
        return AnnotatedValue("RESULT").withFeatures(features)

class DynamicResultAction(Action):
    def onConfigure(self):
        self.argsMeta = [ArgMeta("type", StringType())]
        self.resultMeta = ResultMeta(DynamicType())
    def onCall(self, type):
        if type == "string":
            return DynamicValue("text", StringType())
        elif type == "boolean":
            return DynamicValue(True, BooleanType())
        else:
            return None

class TypeResultAction(Action):
    def onConfigure(self):
        self.argsMeta = [ArgMeta("type", StringType())]
        self.resultMeta = ResultMeta(TypeType())
    def onCall(self, type):
        if type == "string":
            return StringType()
        elif type == "boolean":
            return BooleanType()
        else:
            return None

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
        self.logger.debug("Received event: {}", event)
        sponge.getVariable("eventSent").set(True)

def onAfterReload():
    sponge.getVariable("reloaded").set(True)
