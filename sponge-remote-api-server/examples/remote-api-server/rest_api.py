"""
Sponge Knowledge base
Used for testing a REST API server and clients.
"""

from java.util.concurrent.atomic import AtomicBoolean
from org.apache.commons.io import IOUtils

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

    sponge.addCategories(CategoryMeta("category1").withLabel("Category 1").withDescription("Category 1 description"),
                         CategoryMeta("category2").withLabel("Category 2").withDescription("Category 2 description"))

def onBeforeLoad():
    sponge.addType("Person", lambda: RecordType().withFields([
        StringType("firstName").withLabel("First name"),
        StringType("surname").withLabel("Surname")
    ]))
    sponge.addType("Citizen", lambda: RecordType().withBaseType(sponge.getType("Person")).withFields([
        StringType("country").withLabel("Country")
    ]))

def onLoad():
    sponge.kb.version = 2

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.").withCategory("category1").withVersion(2)
        self.withArg(
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ).withResult(StringType().withLabel("Upper case text"))
    def onCall(self, text):
        sponge.getVariable("actionCalled").set(True)
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to lower case").withDescription("Converts a string to lower case.").withCategory("category1")
        self.withArg(StringType("text").withLabel("A text that will be changed to lower case"))
        self.withResult(StringType().withLabel("Lower case text"))
    def onCall(self, text):
        return text.lower()

class EchoImage(Action):
    def onConfigure(self):
        self.withLabel("Echo an image").withCategory("category2")
        self.withArg(BinaryType("image").withMimeType("image/png").withLabel("Image"))
        self.withResult(BinaryType().withMimeType("image/png").withLabel("Image echo"))
    def onCall(self, image):
        return image

class ListValues(Action):
    def onConfigure(self):
        self.withFeatures({"visible":False}).withNoArgs().withResult(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.withLabel("Action with provided argument").withCategory("category2")
        self.withArg(StringType("value").withLabel("Value").withProvided(ProvidedMeta().withValueSet()))
        self.withResult(StringType().withLabel("Same value"))
    def onCall(self, value):
        return value
    def onProvideArgs(self, context):
        if "value" in context.names:
            context.provided["value"] = ProvidedValue().withValueSet(sponge.call("ListValues"))

class PrivateAction(Action):
    def onCall(self, args):
        return None

class NoMetadataAction(Action):
    def onCall(self, args):
        return None

class KnowledgeBaseErrorAction(Action):
    def onConfigure(self):
        self.withLabel("Knowledge base error action").withNoArgs().withNoResult()
    def onCall(self):
        raise Exception("Knowledge base exception")

class LangErrorAction(Action):
    def onConfigure(self):
        self.withLabel("Language error action").withNoArgs().withNoResult()
    def onCall(self):
        return throws_error

class ComplexObjectAction(Action):
    def onConfigure(self):
        self.withArg(ObjectType("arg").withClassName("org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject"))
        self.withResult(ObjectType().withClassName("org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject"))
    def onCall(self, arg):
        arg.id += 1
        return arg

class ComplexObjectListAction(Action):
    def onConfigure(self):
        self.withArg(ListType("arg").withElement(
            ObjectType().withClassName("org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject")
        ).withLabel("Text to upper case"))
        self.withResult(ListType().withElement(
            ObjectType().withClassName("org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject")
        ))
    def onCall(self, arg):
        arg[0].id += 1
        return arg

class ComplexObjectHierarchyAction(Action):
    def onConfigure(self):
        self.withArgs([
            StringType("stringArg"),
            AnyType("anyArg"),
            ListType("stringListArg", StringType()),
            ListType("decimalListArg", ObjectType().withClassName("java.math.BigDecimal")),
            ObjectType("stringArrayArg").withClassName("java.lang.String[]"),
            MapType("mapArg", StringType(), ObjectType().withClassName("org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject"))
        ]).withResult(ListType(AnyType()))
    def onCall(self, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg):
        self.logger.info("Action {} called: {}, {}, {}, {}, {}, {}", self.meta.name, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg)
        return [stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg]

class SetActuator(Action):
    def onConfigure(self):
        self.withLabel("Set actuator").withDescription("Sets the actuator state.")
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet()),
            BooleanType("actuator2").withLabel("Actuator 2 state").withProvided(ProvidedMeta().withValue()),
            IntegerType("actuator3").withNullable().withLabel("Actuator 3 state").withProvided(ProvidedMeta().withValue().withReadOnly()),
            IntegerType("actuator4").withLabel("Actuator 4 state")
        ]).withNoResult()
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in context.names:
            context.provided["actuator2"] = ProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in context.names:
            context.provided["actuator3"] = ProvidedValue().withValue(sponge.getVariable("actuator3", None))

class SetActuatorNotLimitedValueSet(Action):
    def onConfigure(self):
        self.withLabel("Set actuator not limited value set")
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet(ValueSetMeta().withNotLimited())),
        ]).withNoResult()
    def onCall(self, actuator1):
        pass
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])

class SetActuatorDepends(Action):
    def onConfigure(self):
        self.withLabel("Set actuator with depends").withDescription("Sets the actuator state.")
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet()),
            BooleanType("actuator2").withLabel("Actuator 2 state").withProvided(ProvidedMeta().withValue()),
            IntegerType("actuator3").withLabel("Actuator 3 state").withProvided(ProvidedMeta().withValue()),
            IntegerType("actuator4").withLabel("Actuator 4 state"),
            StringType("actuator5").withLabel("Actuator 5 state").withProvided(ProvidedMeta().withValue().withValueSet().withDependency("actuator1")),
        ]).withNoResult()
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        sponge.setVariable("actuator3", actuator3)
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withAnnotatedValueSet(
                [AnnotatedValue("A").withLabel("Value A"), AnnotatedValue("B").withLabel("Value B"), AnnotatedValue("C").withLabel("Value C")])
        if "actuator2" in context.names:
            context.provided["actuator2"] = ProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in context.names:
            context.provided["actuator3"] = ProvidedValue().withValue(sponge.getVariable("actuator3", None))
        if "actuator5" in context.names:
            context.provided["actuator5"] = ProvidedValue().withValue(sponge.getVariable("actuator5", None)).withValueSet([
                "X", "Y", "Z", context.current["actuator1"]])

class AnnotatedTypeAction(Action):
    def onConfigure(self):
        self.withArg(BooleanType("arg1").withAnnotated().withLabel("Argument 1"))
        self.withResult(StringType().withAnnotated().withLabel("Annotated result"))
    def onCall(self, arg1):
        features = {"feature1":"value1"}
        features.update(arg1.features)
        return AnnotatedValue("RESULT").withFeatures(features)

class DynamicResultAction(Action):
    def onConfigure(self):
        self.withArg(StringType("type")).withResult(DynamicType())
    def onCall(self, type):
        if type == "string":
            return DynamicValue("text", StringType())
        elif type == "boolean":
            return DynamicValue(True, BooleanType())
        else:
            return None

class TypeResultAction(Action):
    def onConfigure(self):
        self.withArg(StringType("type")).withResult(TypeType())
    def onCall(self, type):
        if type == "string":
            return StringType()
        elif type == "boolean":
            return BooleanType()
        else:
            return None

class DateTimeAction(Action):
    def onConfigure(self):
        self.withArgs([
            DateTimeType("dateTime").withDateTime(),
            DateTimeType("dateTimeZone").withDateTimeZone(),
            DateTimeType("date").withDate().withFormat("yyyy-MM-dd"),
            DateTimeType("time").withTime().withFormat("HH:mm:ss"),
            DateTimeType("instant").withInstant()
        ]).withResult(ListType(DynamicType()))
    def onCall(self, dateTime, dateTimeZone, date, time, instant):
        return [DynamicValue(dateTime, self.meta.args[0]), DynamicValue(dateTimeZone, self.meta.args[1]),
                DynamicValue(date, self.meta.args[2]), DynamicValue(time, self.meta.args[3]),
                DynamicValue(instant, self.meta.args[4])]

def createBookType(name):
    return RecordType(name, [
                IntegerType("id").withNullable().withLabel("Identifier"),
                StringType("author").withLabel("Author"),
                StringType("title").withLabel("Title"),
                StringType("comment").withNullable().withLabel("Comment")
            ])

class RecordAsResultAction(Action):
    def onConfigure(self):
        self.withArg(IntegerType("bookId")).withResult(createBookType("book").withNullable())
    def onCall(self, bookId):
        return {"id":bookId, "author":"James Joyce", "title":"Ulysses", "comment":None}

class RecordAsArgAction(Action):
    def onConfigure(self):
        self.withArg(createBookType("book")).withResult(createBookType("book"))
    def onCall(self, book):
        return book

class NestedRecordAsArgAction(Action):
    def onConfigure(self):
        self.withArg(
            RecordType("book").withLabel("Book").withFields([
                IntegerType("id").withNullable().withLabel("Identifier"),
                RecordType("author").withLabel("Author").withFields([
                    IntegerType("id").withNullable().withLabel("Identifier"),
                    StringType("firstName").withLabel("First name"),
                    StringType("surname").withLabel("Surname")
                ]),
                StringType("title").withLabel("Title"),
            ])).withResult(StringType())
    def onCall(self, book):
        return "{} {} - {}".format(book["author"]["firstName"], book["author"]["surname"], book["title"])

class OutputStreamResultAction(Action):
    def onConfigure(self):
        self.withNoArgs().withResult(StreamType())
    def onCall(self):
        return OutputStreamValue(lambda output: IOUtils.write("Sample text file\n", output, "UTF-8")).withContentType("text/plain; charset=\"UTF-8\"").withHeaders({
            })

class RegisteredTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Registered type argument action").withArg(sponge.getType("Person").withName("person")).withResult(StringType())
    def onCall(self, person):
        return person["surname"]

class InheritedRegisteredTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Inherited, registered type argument action").withArg(sponge.getType("Citizen").withName("citizen")).withResult(StringType())
    def onCall(self, citizen):
        return citizen["firstName"] + " comes from " + citizen["country"]

class FruitsElementValueSetAction(Action):
    def onConfigure(self):
        self.withLabel("Fruits action with argument element value set")
        self.withArg(ListType("fruits", StringType()).withLabel("Fruits").withUnique().withProvided(ProvidedMeta().withElementValueSet())).withResult(IntegerType())
    def onCall(self, fruits):
        return len(fruits)
    def onProvideArgs(self, context):
        if "fruits" in context.names:
            context.provided["fruits"] = ProvidedValue().withAnnotatedElementValueSet([
                AnnotatedValue("apple").withLabel("Apple"), AnnotatedValue("banana").withLabel("Banana"), AnnotatedValue("lemon").withLabel("Lemon")
            ])

class RemoteApiIsActionPublic(Action):
    def onCall(self, actionAdapter):
        return not (actionAdapter.meta.name.startswith("Private") or actionAdapter.meta.name.startswith("RemoteApi"))

class RemoteApiIsEventPublic(Action):
    def onCall(self, eventName):
        return True

class Alarm(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        self.logger.debug("Received event: {}", event)
        sponge.getVariable("eventSent").set(True)

def onAfterReload():
    sponge.getVariable("reloaded").set(True)
