"""
Sponge Knowledge base
Provide arguments by action
"""

class GetAvailableSensorNames(Action):
    def onConfigure(self):
        self.withLabel("Get available sensor names").withNoArgs().withResult(ListType(StringType()).withLabel("Available sensors"))
    def onCall(self):
        return ["sensor1", "sensor2"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.withLabel("Action with provided argument")
        self.withArg(StringType("sensorName").withProvided(ProvidedMeta().withValueSet()))
        self.withResult(BooleanType().withLabel("Boolean result"))
    def onCall(self, sensorName):
        return sensorName == "sensor1"
    def onProvideArgs(self, context):
        if "sensorName" in context.names:
            context.provided["sensorName"] = ProvidedValue().withValueSet(sponge.call("GetAvailableSensorNames"))
