"""
Sponge Knowledge base
Demo
"""

from java.lang import String, Boolean
from java.time.format import DateTimeFormatter
from java.time import LocalDateTime
from org.apache.commons.io import IOUtils

def onInit():
    sponge.setVariable("demo.readOnly", Boolean.valueOf(sponge.getProperty("demo.readOnly", "false")))
    if sponge.getVariable("demo.readOnly"):
        sponge.logger.info("RUNNING IN THE READ ONLY MODE")

    sponge.addCategories(
        CategoryMeta("basic").withLabel("Basic"),
        CategoryMeta("forms").withLabel("Forms"),
        CategoryMeta("types").withLabel("Types"),
        CategoryMeta("digits").withLabel("Digits"),
        CategoryMeta("admin").withLabel("Admin"),
        CategoryMeta("plus").withLabel("Extra"),
        CategoryMeta("events").withLabel("Events")
    )

def onLoad():
    sponge.selectCategory("basic", lambda processor: processor.kb.name in ("demo", "engine"))
    sponge.selectCategory("forms", lambda processor: processor.kb.name in ("demoForms", "demoFormsLibraryArgs", "demoFormsLibraryRecord"))
    sponge.selectCategory("types", lambda processor: processor.kb.name in ("types"))
    sponge.selectCategory("digits", lambda processor: processor.kb.name in ("digits"))
    sponge.selectCategory("admin", lambda processor: processor.kb.name in ("admin"))
    sponge.selectCategory("plus", lambda processor: processor.kb.name in ("demoPlus", "digitsLearn"))
    sponge.selectCategory("events", lambda processor: processor.kb.name in ("events", "eventsNotification", "eventsMemo", "eventsCounter"))

class HelloWorldAction(Action):
    def onConfigure(self):
        self.withLabel("Hello world").withDescription("Returns a greeting text.")
        self.withArg(StringType("name").withLabel("Your name").withDescription("Type your name."))
        self.withResult(StringType().withLabel("Greeting").withDescription("The greeting text."))
    def onCall(self, name):
        return u"Hello World! Hello {}!".format(name)

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArg(
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ).withResult(StringType().withLabel("Upper case text"))
        self.withFeatures({"icon":"format-letter-case-upper", "showClear":True, "showCancel":True})
    def onCall(self, text):
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to lower case").withDescription("Converts a string to lower case.")
        self.withArg(StringType("text").withLabel("Text to lower case").withDescription("The text that will be changed to lower case"))
        self.withResult(StringType().withLabel("Lower case text"))
        self.withFeature("icon", "format-letter-case-lower")
    def onCall(self, text):
        return text.lower()

class ListValues(Action):
    def onConfigure(self):
        self.withFeatures({"visible":False})
        self.withNoArgs().withResult(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.withLabel("Action with provided arguments")
        self.withArg(StringType("valueLimited").withLabel("Value limited").withProvided(ProvidedMeta().withValueSet()))
        self.withArg(StringType("valueNotLimited").withLabel("Value not limited").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())))
        self.withArg(StringType("valueLimitedNullable").withLabel("Value limited nullable").withNullable().withProvided(ProvidedMeta().withValueSet()))
        self.withResult(StringType().withLabel("Values"))
    def onCall(self, valueLimited, valueNotLimited, valueLimitedNullable):
        return valueLimited + "/" + valueNotLimited + "/" + str(valueLimitedNullable)
    def onProvideArgs(self, context):
        if "valueLimited" in context.provide:
            context.provided["valueLimited"] = ProvidedValue().withValueSet(sponge.call("ListValues"))
        if "valueNotLimited" in context.provide:
            context.provided["valueNotLimited"] = ProvidedValue().withValueSet(sponge.call("ListValues"))
        if "valueLimitedNullable" in context.provide:
            context.provided["valueLimitedNullable"] = ProvidedValue().withValueSet(sponge.call("ListValues"))

class FruitsElementValueSetAction(Action):
    def onConfigure(self):
        self.withLabel("Fruits action with argument element value set")
        self.withArg(ListType("fruits", StringType()).withLabel("Fruits").withUnique().withProvided(
            ProvidedMeta().withValue().withElementValueSet()))
        self.withResult(IntegerType())
        self.withFeatures({"icon":"apple", "showClear":True})
    def onCall(self, fruits):
        return len(fruits)
    def onProvideArgs(self, context):
        if "fruits" in context.provide:
            context.provided["fruits"] = ProvidedValue().withValue([]).withAnnotatedElementValueSet([
                AnnotatedValue("apple").withValueLabel("Apple"), AnnotatedValue("banana").withValueLabel("Banana"),
                AnnotatedValue("lemon").withValueLabel("Lemon")
            ])

class ConsoleOutput(Action):
    def onConfigure(self):
        self.withLabel("Console output").withDescription("Returns the console output.")
        self.withNoArgs().withResult(StringType().withFormat("console").withLabel("Console output"))
        self.withFeatures({"icon":"console"})
    def onCall(self):
        result = ""
        for i in range(30):
            result += str(i) + ". Row " + str(i) + " - 120\n"
        return result

class MarkdownText(Action):
    def onConfigure(self):
        self.withLabel("Markdown text").withDescription("Returns the markdown text.")
        self.withNoArgs().withResult(StringType().withFormat("markdown").withLabel("Markdown text"))
        self.withFeatures({"icon":"language-markdown"})
    def onCall(self):
        return """Heading
=======

## Sub-heading

Paragraphs are separated by a blank line.

Two spaces at the end of a line produces a line break.

Text attributes _italic_, **bold**, `monospace`.

Horizontal rule:

---

Bullet list:

  * apples
  * oranges
  * pears

Numbered list:

  1. wash
  2. rinse
  3. repeat

```
source code example
```
"""

class DateTimeAction(Action):
    def onConfigure(self):
        self.withLabel("Action with a date/time argument")
        self.withArg(DateTimeType("dateTime").withDateTime().withFormat("yyyy-MM-dd HH:mm").withMinValue(LocalDateTime.of(2020, 1, 1, 0, 0)).withLabel("Date and time"))
        self.withResult(StringType().withLabel("Formatted text"))
        self.withFeature("icon", "timer")
    def onCall(self, dateTime):
        return dateTime.format(DateTimeFormatter.ofPattern(self.meta.args[0].format))

class ManyArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Many arguments action")
        self.withArgs(map(lambda i:  StringType("a" + str(i + 1)).withNullable().withLabel("Argument " + str(i + 1)), range(30)))
        self.withNoResult()
        self.withFeature("icon", "fan")
    def onCall(self, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30):
        return None

class DynamicResultAction(Action):
    def onConfigure(self):
        self.withLabel("Action returning a dynamic result")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
        self.withFeatures({"icon":"fan", "showClear":True, "showCancel":True})
    def onCall(self, type):
        if type == "string":
            return DynamicValue("text", StringType())
        elif type == "boolean":
            return DynamicValue(True, BooleanType())
        elif type == "datetime":
            return DynamicValue(LocalDateTime.now(), DateTimeType().withTime().withFormat("HH:mm"))
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet(["string", "boolean", "datetime"])

class DynamicProvidedArgAction(Action):
    def onConfigure(self):
        self.withLabel("Action with a provided, dynamic argument")
        self.withArg(DynamicType("dynamic").withLabel("Dynamic argument").withProvided(ProvidedMeta().withValue()))
        self.withResult(StringType().withLabel("Dynamic type"))
        self.withFeature("icon", "fan")
    def onCall(self, dynamic):
        return str(dynamic.type.kind)
    def onProvideArgs(self, context):
        if "dynamic" in context.provide:
            context.provided["dynamic"] = ProvidedValue().withValue(DynamicValue({"firstName":"James", "surname":"Joyce"},
                RecordType().withFields([
                    StringType("firstName").withLabel("First name"),
                    StringType("surname").withLabel("Surname")
                ])
            ))

class DynamicProvidedArgActionNestedProvided(Action):
    def onConfigure(self):
        self.withLabel("Action with a provided, dynamic, nested argument (experimental)")
        self.withArg(DynamicType("dynamic").withLabel("Dynamic argument").withProvided(ProvidedMeta().withValue()))
        self.withResult(StringType().withLabel("Summary"))
        self.withFeature("icon", "fan")

    def onCall(self, dynamic):
        return u"{} - Q1A: {}; Q2A: {}.".format(dynamic.value["fullName"], dynamic.value["question1"] if dynamic.value["question1"] else "",
                                              dynamic.value["question2"] if dynamic.value["question2"] else "")

    def onProvideArgs(self, context):
        if "dynamic" in context.provide:
            context.provided["dynamic"] = ProvidedValue().withValue(DynamicValue({},
                RecordType().withFields([
                    StringType("firstName").withLabel("First name"),
                    StringType("surname").withLabel("Surname"),
                    StringType("fullName").withLabel("Full name").withNullable().withReadOnly().withProvided(
                        ProvidedMeta().withValue().withDependencies([
                            "dynamic.firstName", "dynamic.surname"])),
                    StringType("question1").withLabel("Question 1").withNullable(),
                    StringType("question2").withLabel("Question 2").withNullable()
                ])
            ))
        if "dynamic.fullName" in context.provide:
            context.provided["dynamic.fullName"] = ProvidedValue().withValue(
                "{} {}".format(context.current["dynamic.firstName"], context.current["dynamic.surname"]))

class RecordResultAction(Action):
    def onConfigure(self):
        self.withLabel("Action returning a record")
        self.withNoArgs().withResult(RecordType().withLabel("Book").withFields([
                StringType("author").withLabel("Author"),
                StringType("title").withLabel("Title"),
            ]))
        self.withFeature("icon", "fan")
    def onCall(self):
        return {"author":"James Joyce", "title":"Ulysses"}

class ObscuredTextArgAction(Action):
    def onConfigure(self):
        self.withLabel("Action with an obscured text argument").withArgs([
            StringType("plainText").withLabel("Plain text"),
            StringType("obscuredText").withLabel("Obscured text").withFeature("obscure", True)
        ]).withResult(StringType().withLabel("Obscured text").withFeature("obscure", True))
    def onCall(self, plainText, obscuredText):
        return obscuredText

# Unsupported by the mobile client application.
class OutputStreamResultAction(Action):
    def onConfigure(self):
        self.withLabel("Unsupported action using a stream").withNoArgs().withResult(StreamType())
    def onCall(self):
        return OutputStreamValue(lambda output: IOUtils.write("Sample text file\n", output, "UTF-8")).withContentType("text/plain; charset=\"UTF-8\"")

class LowerCaseHello(Action):
    def onConfigure(self):
        self.withLabel("Hello with lower case")
        self.withArg(StringType("text").withLabel("Text to lower case")).withResult(StringType().withLabel("Lower case text"))
    def onCall(self, text):
        return "Hello " + remoteApiServer.session.userAuthentication.userContext.name + ": " + text.lower()

class DrawDoodle(Action):
    def onConfigure(self):
        self.withLabel("Draw a doodle").withDescription("Shows a canvas to draw a doodle")
        self.withArg(BinaryType("image").withLabel("Doodle").withMimeType("image/png")
                     .withFeatures({"characteristic":"drawing", "width":300, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":2}))
        self.withNoResult().withFeatures({"icon":"brush", "callLabel":"OK", "showClear":True, "showCancel":True})
    def onCall(self, image):
        pass
