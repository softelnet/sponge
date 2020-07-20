"""
Sponge Knowledge Base
Light sensor action with a refresh event
"""

class ViewLightStatusWithAutoRefresh(Action):
    def onConfigure(self):
        self.withLabel("View the light status (with auto refresh)")
        self.withDescription("Provides the light status and refreshes it automatically.")
        self.withArgs([
            BooleanType("light").withNullable().withLabel("Light").withReadOnly().withProvided(ProvidedMeta().withValue())
        ]).withNonCallable()
        self.withFeatures({"cancelLabel":"Close", "icon":"lightbulb-outline", "refreshEvents":["lightNotification"]})
    def onProvideArgs(self, context):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        if "light" in context.provide:
            context.provided["light"] =  ProvidedValue().withValue(sponge.call("IsLight", [grovePiDevice.getLightSensor()]))

class IsLight(Action):
    def onInit(self):
        self.lightThreshold = 100.0
    def onCall(self, lightSensorValue):
        return lightSensorValue > self.lightThreshold