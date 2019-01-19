"""
Sponge Knowledge base
Provide arguments by action
"""

class GetAvailableSensorNames(Action):
    def onConfigure(self):
        self.displayName = "Get available sensor names"
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType())).displayName("Available sensors")
    def onCall(self):
        return ["sensor1", "sensor2"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.displayName = "Action with provided argument"
        self.argsMeta = [ ArgMeta("sensorName", StringType()).provided(ArgProvided().valueSet()) ]
        self.resultMeta = ResultMeta(BooleanType()).displayName("Boolean result")
    def onCall(self, sensorName):
        return sensorName == "sensor1"
    def onProvideArgs(self, names, current, provided):
        if "sensorName" in names:
            provided["sensorName"] = ArgValue().valueSet(sponge.call("GetAvailableSensorNames"))
