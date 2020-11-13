"""
Sponge Knowledge Base
Demo
"""

class DependingArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Depending arguments")
        self.withArgs([
            StringType("continent").withLabel("Continent").withProvided(ProvidedMeta().withValueSet()),
            StringType("country").withLabel("Country").withProvided(ProvidedMeta().withValueSet().withDependency("continent")),
            StringType("city").withLabel("City").withProvided(ProvidedMeta().withValueSet().withDependency("country")),
            StringType("river").withLabel("River").withProvided(ProvidedMeta().withValueSet().withDependency("continent")),
            StringType("weather").withLabel("Weather").withProvided(ProvidedMeta().withValueSet())
        ]).withResult(StringType().withLabel("Sentences"))
        self.withFeatures({"icon":"flag", "showClear":True, "showCancel":True})
    def onCall(self, continent, country, city, river, weather):
        return "There is a city {} in {} in {}. The river {} flows in {}. It's {}.".format(city, country, continent, river, continent, weather.lower())
    def onInit(self):
        self.countries = {
            "Africa":["Nigeria", "Ethiopia", "Egypt"],
            "Asia":["China", "India", "Indonesia"],
            "Europe":["Russia", "Germany", "Turkey"]
        }
        self.cities = {
            "Nigeria":["Lagos", "Kano", "Ibadan"],
            "Ethiopia":["Addis Ababa", "Gondar", "Mek'ele"],
            "Egypt":["Cairo", "Alexandria", "Giza"],
            "China":["Guangzhou", "Shanghai", "Chongqing"],
            "India":["Mumbai", "Delhi", "Bangalore"],
            "Indonesia":["Jakarta", "Surabaya", "Medan"],
            "Russia":["Moscow", "Saint Petersburg", "Novosibirsk"],
            "Germany":["Berlin", "Hamburg", "Munich"],
            "Turkey":["Istanbul", "Ankara", "Izmir"]
        }
        self.rivers = {
             "Africa":["Nile", "Chambeshi", "Niger"],
            "Asia":["Yangtze", "Yellow River", "Mekong"],
            "Europe":["Volga", "Danube", "Dnepr"]
        }
    def onProvideArgs(self, context):
        if "continent" in context.provide:
            context.provided["continent"] = ProvidedValue().withValueSet(["Africa", "Asia", "Europe"])
        if "country" in context.provide:
            context.provided["country"] = ProvidedValue().withValueSet(self.countries.get(context.current["continent"], []))
        if "city" in context.provide:
            context.provided["city"] = ProvidedValue().withValueSet(self.cities.get(context.current["country"], []))
        if "river" in context.provide:
            context.provided["river"] = ProvidedValue().withValueSet(self.rivers.get(context.current["continent"], []))
        if "weather" in context.provide:
            context.provided["weather"] = ProvidedValue().withValueSet(["Sunny", "Cloudy", "Raining", "Snowing"])
