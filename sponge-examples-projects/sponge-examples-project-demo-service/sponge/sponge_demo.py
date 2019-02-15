"""
Sponge Knowledge base
Demo
"""

from java.lang import String
from org.openksavi.sponge.util.process import ProcessConfiguration
from java.time.format import DateTimeFormatter

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArg(
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        )
        self.withResult(ResultMeta(StringType()).withLabel("Upper case text"))
    def onCall(self, text):
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to lower case").withDescription("Converts a string to lower case.")
        self.withArg(ArgMeta("text", StringType()).withLabel("Text to lower case").withDescription("The text that will be changed to lower case"))
        self.withResult(ResultMeta(StringType()).withLabel("Lower case text"))
    def onCall(self, text):
        return text.lower()

class ListValues(Action):
    def onConfigure(self):
        self.withFeatures({"visible":False})
        self.withNoArgs().withResult(ResultMeta(ListType(StringType())))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.withLabel("Action with provided argument")
        self.withArg(ArgMeta("value", StringType()).withLabel("Value").withProvided(ArgProvidedMeta().withValueSet()))
        self.withResult(ResultMeta(StringType()).withLabel("Same value"))
    def onCall(self, value):
        return value
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValueSet(sponge.call("ListValues"))

class ChooseColor(Action):
    def onConfigure(self):
        self.withLabel("Choose a color").withDescription("Shows a color argument.")
        self.withArg(
            ArgMeta("color", StringType().withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"}))
                .withLabel("Color").withDescription("The color.")
        )
        self.withResult(ResultMeta(StringType()))
    def onCall(self, color):
        return "The chosen color is " + color

class ConsoleOutput(Action):
    def onConfigure(self):
        self.withLabel("Console output").withDescription("Returns the console output.")
        self.withNoArgs().withResult(ResultMeta(StringType().withFormat("console")).withLabel("Console output"))
    def onCall(self):
        result = ""
        for i in range(30):
            result += str(i) + ". Row " + str(i) + " - 120\n"
        return result

class MarkdownText(Action):
    def onConfigure(self):
        self.withLabel("Markdown text").withDescription("Returns the markdown text.")
        self.withNoArgs().withResult(ResultMeta(StringType().withFormat("markdown")).withLabel("Markdown text"))
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
        self.withNoArgs().withResult(ResultMeta(BinaryType().withMimeType("text/html")).withLabel("HTML file"))
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
        self.withNoArgs().withResult(ResultMeta(BinaryType().withMimeType("application/pdf")).withLabel("PDF file"))
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("curl", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                              .outputAsBinary()).run().outputBinary

class DependingArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Action with depending arguments")
        self.withArgs([
            ArgMeta("continent", StringType()).withLabel("Continent").withProvided(ArgProvidedMeta().withValueSet()),
            ArgMeta("country", StringType()).withLabel("Country").withProvided(ArgProvidedMeta().withValueSet().withDependency("continent")),
            ArgMeta("city", StringType()).withLabel("City").withProvided(ArgProvidedMeta().withValueSet().withDependency("country")),
            ArgMeta("river", StringType()).withLabel("River").withProvided(ArgProvidedMeta().withValueSet().withDependency("continent")),
            ArgMeta("weather", StringType()).withLabel("Weather").withProvided(ArgProvidedMeta().withValueSet())
        ])
        self.withResult(ResultMeta(StringType()).withLabel("Sentences"))
    def onCall(self, continent, country, city, river, weather):
        return "There is a city {} in {} in {}. The river {} flows in {}. It's {}.".format(city, country, continent, river, continent, weather.lower())
    def onProvideArgs(self, names, current, provided):
        if "continent" in names:
            provided["continent"] = ArgProvidedValue().withValueSet(["Africa", "Asia", "Europe"])
        if "country" in names:
            continent = current["continent"]
            if continent == "Africa":
                countries = ["Nigeria", "Ethiopia", "Egypt"]
            elif continent == "Asia":
                countries = ["China", "India", "Indonesia"]
            elif continent == "Europe":
                countries = ["Russia", "Germany", "Turkey"]
            else:
                countries = []
            provided["country"] = ArgProvidedValue().withValueSet(countries)
        if "city" in names:
            country = current["country"]
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
            provided["city"] = ArgProvidedValue().withValueSet(cities)
        if "river" in names:
            continent = current["continent"]
            if continent == "Africa":
                rivers = ["Nile", "Chambeshi", "Niger"]
            elif continent == "Asia":
                rivers = ["Yangtze", "Yellow River", "Mekong"]
            elif continent == "Europe":
                rivers = ["Volga", "Danube", "Dnepr"]
            else:
                rivers = []
            provided["river"] = ArgProvidedValue().withValueSet(rivers)
        if "weather" in names:
            provided["weather"] = ArgProvidedValue().withValueSet(["Sunny", "Cloudy", "Raining", "Snowing"])

class DateTimeAction(Action):
    def onConfigure(self):
        self.withLabel("Action with a date/time argument")
        self.withArg(ArgMeta("dateTime", DateTimeType().withDateTime().withFormat("yyyy-MM-dd HH:mm")).withLabel("Date and time"))
        self.withResult(ResultMeta(StringType()).withLabel("Formatted text"))
    def onCall(self, dateTime):
        return dateTime.format(DateTimeFormatter.ofPattern(self.meta.argsMeta[0].type.format))

class ManyArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Many arguments action")
        self.withArgs(map(lambda i:  ArgMeta("a" + str(i + 1), StringType().withNullable()).withLabel("Argument " + str(i + 1)), range(30)))
        self.withNoResult()
    def onCall(self, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30):
        return None
