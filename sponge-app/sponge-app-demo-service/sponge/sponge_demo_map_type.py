"""
Sponge Knowledge base
Demo - A map type
"""

class ActionWithMapTypeResult(Action):
    def onConfigure(self):
        self.withLabel("Map type result")
        self.withArg(StringType("data").withLabel("Map data").withProvided(ProvidedMeta().withValueSet())).withResult(
            MapType().withLabel("Map")
                .withKey(IntegerType("key").withLabel("Key"))
                .withValue(StringType("value").withLabel("Value")))
    def onCall(self, data):
        if data == "data":
            return {1:"One", 2:"Two", 3:"Three", 4:"Four", 5:"Five"}
        elif data == 'noData':
            return {}
        elif data == 'null':
            return None
        return data
    def onProvideArgs(self, context):
        if "data" in context.provide:
            context.provided["data"] = ProvidedValue().withAnnotatedValueSet([
                AnnotatedValue("data").withValueLabel("Map with data"),
                AnnotatedValue("noData").withValueLabel("Map with no data"),
                AnnotatedValue("null").withValueLabel("Map with a null value")
            ])