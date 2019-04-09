"""
Sponge Knowledge base
Demo
"""

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
