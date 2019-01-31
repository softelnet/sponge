"""
Sponge Knowledge base
Demo
"""

from java.lang import String
from org.openksavi.sponge.util.process import ProcessConfiguration

class UpperCase(Action):
    def onConfigure(self):
        self.label = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256)).label("Text to upper case").description("The text that will be converted to upper case.")
        ]
        self.resultMeta = ResultMeta(StringType()).label("Upper case text")
    def onCall(self, text):
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.label = "Convert to lower case"
        self.description = "Converts a string to lower case."
        self.argsMeta = [ ArgMeta("text", StringType()).label("Text to lower case").description("The text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(StringType()).label("Lower case text")
    def onCall(self, text):
        return text.lower()

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
        self.argsMeta = [ArgMeta("value", StringType()).label("Value").provided(ArgProvided().valueSet())]
        self.resultMeta = ResultMeta(StringType()).label("Same value")
    def onCall(self, value):
        return value
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgValue().withValueSet(sponge.call("ListValues"))

class ChooseColor(Action):
    def onConfigure(self):
        self.label = "Choose a color"
        self.description = "Shows a color argument."
        self.argsMeta = [
            ArgMeta("color", StringType().maxLength(6).nullable(True).features({"characteristic":"color"}))
                .label("Color").description("The color.")
        ]
        self.resultMeta = ResultMeta(StringType())
    def onCall(self, color):
        return "The chosen color is " + color

class ConsoleOutput(Action):
    def onConfigure(self):
        self.label = "Console output"
        self.description = "Returns the console output."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().format("console")).label("Console output")
    def onCall(self):
        result = ""
        for i in range(30):
            result += str(i) + ". Row " + str(i) + " - 120\n"
        return result

class MarkdownText(Action):
    def onConfigure(self):
        self.label = "Markdown text"
        self.description = "Returns the markdown text."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().format("markdown")).label("Markdown text")
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
        self.label = "HTML file output"
        self.description = "Returns the HTML file."
        self.argsMeta = []
        self.resultMeta = ResultMeta(BinaryType().mimeType("text/html")).label("HTML file")
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
        self.label = "PDF file output"
        self.description = "Returns the PDF file."
        self.argsMeta = []
        self.resultMeta = ResultMeta(BinaryType().mimeType("application/pdf")).label("PDF file")
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("curl", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                              .outputAsBinary()).run().outputBinary

class DependingArgumentsAction(Action):
    def onConfigure(self):
        self.label = "Action with depending arguments"
        self.argsMeta = [
            ArgMeta("continent", StringType()).label("Continent").provided(ArgProvided().valueSet()),
            ArgMeta("country", StringType()).label("Country").provided(ArgProvided().valueSet().depends("continent")),
            ArgMeta("city", StringType()).label("City").provided(ArgProvided().valueSet().depends("country")),
            ArgMeta("river", StringType()).label("River").provided(ArgProvided().valueSet().depends("continent")),
            ArgMeta("weather", StringType()).label("Weather").provided(ArgProvided().valueSet()),
        ]
        self.resultMeta = ResultMeta(StringType()).label("Sentences")
    def onCall(self, continent, country, city, river, weather):
        return "There is a city {} in {} in {}. The river {} flows in {}. It's {}.".format(city, country, continent, river, continent, weather.lower())
    def onProvideArgs(self, names, current, provided):
        if "continent" in names:
            provided["continent"] = ArgValue().withValueSet(["Africa", "Asia", "Europe"])
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
            provided["country"] = ArgValue().withValueSet(countries)
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
            provided["city"] = ArgValue().withValueSet(cities)
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
            provided["river"] = ArgValue().withValueSet(rivers)
        if "weather" in names:
            provided["weather"] = ArgValue().withValueSet(["Sunny", "Cloudy", "Raining", "Snowing"])
