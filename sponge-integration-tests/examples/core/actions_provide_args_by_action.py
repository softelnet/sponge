"""
Sponge Knowledge base
Provide arguments by action
"""

class GetAvailableSensorNames(Action):
    def onConfigure(self):
        self.label = "Get available sensor names"
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType())).withLabel("Available sensors")
    def onCall(self):
        return ["sensor1", "sensor2"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.label = "Action with provided argument"
        self.argsMeta = [ ArgMeta("sensorName", StringType()).withProvided(ArgProvidedMeta().withValueSet()) ]
        self.resultMeta = ResultMeta(BooleanType()).withLabel("Boolean result")
    def onCall(self, sensorName):
        return sensorName == "sensor1"
    def onProvideArgs(self, names, current, provided):
        if "sensorName" in names:
            provided["sensorName"] = ArgProvidedValue().withValueSet(sponge.call("GetAvailableSensorNames"))
