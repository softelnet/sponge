"""
Sponge Knowledge Base
gRPC.
"""

def onBeforeLoad():
    sponge.addEventType("lightNotification", RecordType().withFields([
        BooleanType("light").withLabel("Light")
    ]).withLabel("Light notification"))

class LightNotificationSender(Trigger):
    def onConfigure(self):
        self.withEvent("sensorChange")
    def onInit(self):
        self.lastLightOn = None
    def onRun(self, event):
        lightSensorValue = event.get("light", None)

        if lightSensorValue:
            lightOn = sponge.call("IsLight", [lightSensorValue])
            if self.lastLightOn is None or self.lastLightOn != lightOn:
                self.lastLightOn = lightOn
                sponge.event("lightNotification").set({"light":lightOn}).label("Light " + ("on" if lightOn else "off") + "(" + str(lightSensorValue) + ")").send()

def onStartup():
    # Enable support actions in this knowledge base.
    grpcApiServer.enableSupport(sponge)
