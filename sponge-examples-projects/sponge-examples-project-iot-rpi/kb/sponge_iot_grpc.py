"""
Sponge Knowledge base
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
        self.darkThreshold = 100.0
        self.lastDark = None
    def onRun(self, event):
        light = event.get("light", None)

        if light:
            dark = light < self.darkThreshold
            if self.lastDark is None or self.lastDark != dark:
                self.lastDark = dark
                sponge.event("lightNotification").set({"light":(not dark)}).label("Light " + ("off" if dark else "on") + "(" + str(light) + ")").send()

def onStartup():
    # Enable support actions in this knowledge base.
    grpcApiServer.enableSupport(sponge)
