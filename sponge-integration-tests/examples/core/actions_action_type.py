"""
Sponge Knowledge base
Action type
"""

class GetAvailableSensorNames(Action):
    def onConfigure(self):
        self.displayName = "Get available sensor names"
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType())).displayName("Available sensors")
    def onCall(self):
        return ["sensor1", "sensor2"]

class ActionTypeAction(Action):
    def onConfigure(self):
        self.displayName = "Action type example action"
        self.argsMeta = [ ArgMeta("sensorName", ActionType("GetAvailableSensorNames")) ]
        self.resultMeta = ResultMeta(BooleanType()).displayName("Boolean result")
    def onCall(self, sensorName):
        return sensorName == "sensor1"

