"""
Sponge Knowledge base
Demo
"""

from java.lang import String
from org.openksavi.sponge.util.process import ProcessConfiguration
from java.time.format import DateTimeFormatter
from java.time import LocalDateTime

def onInit():
    sponge.addCategories(
        CategoryMeta("basic").withLabel("Basic"),
        CategoryMeta("forms").withLabel("Forms"),
        CategoryMeta("digits").withLabel("Digits"),
        CategoryMeta("admin").withLabel("Admin"),
        CategoryMeta("plus").withLabel("Extra")
    )

def onLoad():
    sponge.selectCategory("basic", lambda processor: processor.kb.name in ("demo", "engine"))
    sponge.selectCategory("forms", lambda processor: processor.kb.name in ("demoForms", "demoFormsLibraryArgs", "demoFormsLibraryRecord"))
    sponge.selectCategory("digits", lambda processor: processor.kb.name in ("digits"))
    sponge.selectCategory("admin", lambda processor: processor.kb.name in ("admin"))
    sponge.selectCategory("plus", lambda processor: processor.kb.name in ("demoPlus", "digitsLearn"))

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArg(
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ).withResult(StringType().withLabel("Upper case text"))
        self.withFeature("icon", "format-letter-case-upper")
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
        self.withResult(StringType().withLabel("Same value"))
    def onCall(self, valueLimited, valueNotLimited):
        return valueLimited + "/" + valueNotLimited
    def onProvideArgs(self, context):
        if "valueLimited" in context.names:
            context.provided["valueLimited"] = ProvidedValue().withValueSet(sponge.call("ListValues"))
        if "valueNotLimited" in context.names:
            context.provided["valueNotLimited"] = ProvidedValue().withValueSet(sponge.call("ListValues"))

class ChooseColor(Action):
    def onConfigure(self):
        self.withLabel("Choose a color").withDescription("Shows a color argument.")
        self.withArg(
            StringType("color").withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"})
                .withLabel("Color").withDescription("The color.")
        ).withResult(StringType())
        self.withFeatures({"icon":"format-color-fill"})
    def onCall(self, color):
        return ("The chosen color is " + color) if color else "No color chosen"

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
        self.withFeatures({"icon":"markdown"})
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

class HtmlFileOutput(Action):
    def onConfigure(self):
        self.withLabel("HTML file output").withDescription("Returns the HTML file.")
        self.withNoArgs().withResult(BinaryType().withMimeType("text/html").withLabel("HTML file"))
        self.withFeatures({"icon":"web"})
    def onCall(self):
        return String("""
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
    <head>
      <title>HTML page</title>
    </head>
    <body>
        <!-- Main content -->
        <h1>Header</h1>
        <p>Some text
    </body>
</html>
""").getBytes("UTF-8")

class PdfFileOutput(Action):
    def onConfigure(self):
        self.withLabel("PDF file output").withDescription("Returns the PDF file.")
        self.withNoArgs().withResult(BinaryType().withMimeType("application/pdf").withLabel("PDF file"))
        self.withFeatures({"icon":"file-pdf"})
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("curl", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                              .outputAsBinary()).run().outputBinary

class DependingArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Action with depending arguments")
        self.withArgs([
            StringType("continent").withLabel("Continent").withProvided(ProvidedMeta().withValueSet()),
            StringType("country").withLabel("Country").withProvided(ProvidedMeta().withValueSet().withDependency("continent")),
            StringType("city").withLabel("City").withProvided(ProvidedMeta().withValueSet().withDependency("country")),
            StringType("river").withLabel("River").withProvided(ProvidedMeta().withValueSet().withDependency("continent")),
            StringType("weather").withLabel("Weather").withProvided(ProvidedMeta().withValueSet())
        ]).withResult(StringType().withLabel("Sentences"))
        self.withFeature("icon", "flag")
    def onCall(self, continent, country, city, river, weather):
        return "There is a city {} in {} in {}. The river {} flows in {}. It's {}.".format(city, country, continent, river, continent, weather.lower())
    def onProvideArgs(self, context):
        if "continent" in context.names:
            context.provided["continent"] = ProvidedValue().withValueSet(["Africa", "Asia", "Europe"])
        if "country" in context.names:
            continent = context.current["continent"]
            if continent == "Africa":
                countries = ["Nigeria", "Ethiopia", "Egypt"]
            elif continent == "Asia":
                countries = ["China", "India", "Indonesia"]
            elif continent == "Europe":
                countries = ["Russia", "Germany", "Turkey"]
            else:
                countries = []
            context.provided["country"] = ProvidedValue().withValueSet(countries)
        if "city" in context.names:
            country = context.current["country"]
            if country == "Nigeria":
                cities = ["Lagos", "Kano", "Ibadan"]
            elif country == "Ethiopia":
                cities = ["Addis Ababa", "Gondar", "Mek'ele"]
            elif country == "Egypt":
                cities = ["Cairo", "Alexandria", "Giza"]
            elif country == "China":
                cities = ["Guangzhou", "Shanghai", "Chongqing"]
            elif country == "India":
                cities = ["Mumbai", "Delhi", "Bangalore"]
            elif country == "Indonesia":
                cities = ["Jakarta", "Surabaya", "Medan"]
            elif country == "Russia":
                cities = ["Moscow", "Saint Petersburg", "Novosibirsk"]
            elif country == "Germany":
                cities = ["Berlin", "Hamburg", "Munich"]
            elif country == "Turkey":
                cities = ["Istanbul", "Ankara", "Izmir"]
            else:
                cities = []
            context.provided["city"] = ProvidedValue().withValueSet(cities)
        if "river" in context.names:
            continent = context.current["continent"]
            if continent == "Africa":
                rivers = ["Nile", "Chambeshi", "Niger"]
            elif continent == "Asia":
                rivers = ["Yangtze", "Yellow River", "Mekong"]
            elif continent == "Europe":
                rivers = ["Volga", "Danube", "Dnepr"]
            else:
                rivers = []
            context.provided["river"] = ProvidedValue().withValueSet(rivers)
        if "weather" in context.names:
            context.provided["weather"] = ProvidedValue().withValueSet(["Sunny", "Cloudy", "Raining", "Snowing"])

class DateTimeAction(Action):
    def onConfigure(self):
        self.withLabel("Action with a date/time argument")
        self.withArg(DateTimeType("dateTime").withDateTime().withFormat("yyyy-MM-dd HH:mm").withLabel("Date and time"))
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
        self.withFeature("icon", "fan")
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
        if "type" in context.names:
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
        if "dynamic" in context.names:
            context.provided["dynamic"] = ProvidedValue().withValue(DynamicValue(
                {"firstName":"James", "surname":"Joyce"},
                RecordType().withFields([
                    StringType("firstName").withLabel("First name"),
                    StringType("surname").withLabel("Surname")
                ])
            ))

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
            StringType("obscuredText").withLabel("Obscured same text").withFeature("obscure", True)
        ]).withResult(StringType().withLabel("Obscured text").withFeature("obscure", True))
    def onCall(self, plainText, obscuredText):
        return obscuredText

class ActionWithContextActions(Action):
    def onConfigure(self):
        self.withLabel("Action with context actions").withArgs([
            StringType("arg1").withLabel("Argument 1"),
            StringType("arg2").withLabel("Argument 2")
        ]).withNoResult().withFeature("contextActions", [
            "ActionWithContextActionsContextAction1", "ActionWithContextActionsContextAction2(arg2)", "ActionWithContextActionsContextAction3(arg2=arg2)",
            "ActionWithContextActionsContextAction4(arg1)", "ActionWithContextActionsContextAction5"
        ])
        self.withFeature("icon", "attachment")
    def onCall(self, arg1, arg2):
        pass

class ActionWithContextActionsContextAction1(Action):
    def onConfigure(self):
        self.withLabel("Context action 1").withArgs([
            RecordType("arg").withFields([
                StringType("arg1").withLabel("Argument 1"),
                StringType("arg2").withLabel("Argument 2")
            ])
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg):
        return arg["arg1"]

class ActionWithContextActionsContextAction2(Action):
    def onConfigure(self):
        self.withLabel("Context action 2").withArgs([
            StringType("arg").withLabel("Argument"),
            StringType("additionalText").withLabel("Additional text"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg, additionalText):
        return arg + " " + additionalText

class ActionWithContextActionsContextAction3(Action):
    def onConfigure(self):
        self.withLabel("Context action 3").withArgs([
            StringType("arg1").withLabel("Argument 1"),
            StringType("arg2").withLabel("Argument 2"),
            StringType("additionalText").withLabel("Additional text"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg1, arg2, additionalText):
        return arg1 + " " + arg2 + " " + additionalText

class ActionWithContextActionsContextAction4(Action):
    def onConfigure(self):
        self.withLabel("Context action 4").withArgs([
            StringType("arg1NotVisible").withLabel("Argument 1 not visible").withFeatures({"visible":False}),
            StringType("arg2").withLabel("Argument 2"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg1NotVisible, arg2):
        return arg1NotVisible + " " + arg2

class ActionWithContextActionsContextAction5(Action):
    def onConfigure(self):
        self.withLabel("Context action 5").withArgs([
            RecordType("arg").withFields([
                StringType("arg1").withLabel("Argument 1"),
                StringType("arg2").withLabel("Argument 2")
            ]).withFeatures({"visible":False}),
            StringType("additionalText").withLabel("Additional text")
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg, additionalText):
        return arg["arg1"] + " " + additionalText
