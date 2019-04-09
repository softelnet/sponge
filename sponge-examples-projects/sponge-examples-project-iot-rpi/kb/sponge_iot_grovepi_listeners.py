"""
Sponge Knowledge base
GrovePi
"""

class DhtSensorListener(Correlator):
    def onConfigure(self):
        self.withEvent("dhtSensorListener").withMaxInstances(1)
    def onInit(self):
        self.temperature = None
        self.humidity = None
        self.publishOnlyChanges = True
    def onEvent(self, event):
        try:
            grovePiDevice = sponge.getVariable("grovePiDevice")
            dht = grovePiDevice.getTemperatureHumiditySensor()
            newTemperature = dht.temperature if dht else None
            newHumidity = dht.humidity if dht else None
            if newTemperature is None or newHumidity is None:
                return
            if sponge.getVariable("grovePiMode", None) == "auto":
                grovePiDevice.setLcd("Temp: {}C\nHumidity: {}%".format(str(int(newTemperature)), str(int(newHumidity))), None)
            if newTemperature != self.temperature or not self.publishOnlyChanges:
                sponge.event("sensorChange").set("temperature", newTemperature).send()
            if newHumidity != self.humidity or not self.publishOnlyChanges:
                sponge.event("sensorChange").set("humidity", newHumidity).send()
            self.temperature = newTemperature
            self.humidity = newHumidity
        finally:
            # Continue the event chain.
            sponge.event(event.name).sendAfter(Duration.ofSeconds(1))

class RotarySensorListener(Trigger):
    def onConfigure(self):
        self.withEvent("rotarySensorListener")

    def onRun(self, event):
        try:
            if sponge.getVariable("grovePiMode", None) == "auto":
                grovePiDevice = sponge.getVariable("grovePiDevice")
                grovePiDevice.setBlueLed(int(grovePiDevice.getRotarySensor().factor * 255.0))
        finally:
            # Continue the event chain.
            sponge.event(event.name).sendAfter(100)

class LightSensorListener(Correlator):
    def onConfigure(self):
        self.withEvent("lightSensorListener").withMaxInstances(1)
    def onInit(self):
        self.light = None
        self.darkThreshold = 20.0
        self.publishOnlyChanges = True
    def onEvent(self, event):
        try:
            grovePiDevice = sponge.getVariable("grovePiDevice")
            newLight = grovePiDevice.getLightSensor()
            if newLight != self.light or not self.publishOnlyChanges:
                sponge.event("sensorChange").set("light", newLight).send()
            self.light = newLight
            if sponge.getVariable("grovePiMode", None) == "auto":
                # Set red led.
                grovePiDevice.setRedLed(newLight < self.darkThreshold)
        finally:
            # Continue the event chain.
            sponge.event(event.name).sendAfter(Duration.ofSeconds(1))
