"""
Sponge Knowledge base
GrovePi
"""

def onStartup():
    sponge.setVariable("grovePiDevice", GrovePiDevice())
    # Grove Pi mode: auto, manual
    sponge.setVariable("grovePiMode", "auto")

class SetGrovePiMode(Action):
    def onConfigure(self):
        self.withLabel("Set the GrovePi mode").withDescription("Sets the GrovePi mode.")
        self.withArg(StringType("mode").withLabel("The GrovePi mode").withProvided(ProvidedMeta().withValue().withValueSet().withOverwrite()))
        self.withNoResult()
        self.withFeature("icon", "settings")
    def onCall(self, mode):
        if mode not in ["auto", "manual"]:
            raise Exception("Unsupported GrovePi mode: " + mode)
        sponge.setVariable("grovePiMode", mode)
    def onProvideArgs(self, context):
        if "mode" in context.names:
            context.provided["mode"] = ProvidedValue().withValue(sponge.getVariable("grovePiMode", None)).withAnnotatedValueSet([
                AnnotatedValue("auto").withLabel("Auto"), AnnotatedValue("manual").withLabel("Manual")])

class ManageLcd(Action):
    def onConfigure(self):
        self.withLabel("Manage the LCD text and color")
        self.withDescription("Provides management of the LCD properties (display text and color). A null value doesn't change an LCD property.")
        self.withArgs([
            StringType("currentText").withMaxLength(256).withNullable(True).withFeatures({"maxLines":2})
                .withLabel("Current LCD text").withDescription("The currently displayed LCD text.").withProvided(ProvidedMeta().withValue().withReadOnly()),
            StringType("text").withMaxLength(256).withNullable(True).withFeatures({"maxLines":2})
                .withLabel("Text to display").withDescription("The text that will be displayed in the LCD.").withProvided(ProvidedMeta().withValue()),
            StringType("color").withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"})
                .withLabel("LCD color").withDescription("The LCD color.").withProvided(ProvidedMeta().withValue().withOverwrite()),
            BooleanType("clearText").withNullable(True).withDefaultValue(False)
                .withLabel("Clear text").withDescription("The text the LCD will be cleared.")
        ]).withNoResult()
        self.withFeature("icon", "monitor")
    def onCall(self, currentText, text, color, clearText = None):
        sponge.call("SetLcd", [text, color, clearText])
    def onProvideArgs(self, context):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        if "currentText" in context.names:
            context.provided["currentText"] = ProvidedValue().withValue(grovePiDevice.getLcdText())
        if "text" in context.names:
            context.provided["text"] = ProvidedValue().withValue(grovePiDevice.getLcdText())
        if "color" in context.names:
            context.provided["color"] = ProvidedValue().withValue(grovePiDevice.getLcdColor())

class SetLcd(Action):
    def onCall(self, text, color, clearText = None):
        sponge.getVariable("grovePiDevice").setLcd("" if (clearText or text is None) else text, color)

class GetLcdText(Action):
    def onConfigure(self):
        self.withLabel("Get the LCD text").withDescription("Returns the LCD text.")
        self.withNoArgs().withResult(StringType().withFeatures({"maxLines":5}).withLabel("LCD Text"))
        self.withFeature("icon", "monitor-dashboard")
    def onCall(self):
        return sponge.getVariable("grovePiDevice").getLcdText()

class GetSensorActuatorValues(Action):
    def onCall(self, names):
        values = {}
        grovePiDevice = sponge.getVariable("grovePiDevice")
        if "temperatureSensor" or "humiditySensor" in names:
            th = grovePiDevice.getTemperatureHumiditySensor()
            if "temperatureSensor" in names:
                values["temperatureSensor"] = th.temperature if th else None
            if "humiditySensor" in names:
                values["humiditySensor"] = th.humidity if th else None
        if "lightSensor" in names:
            values["lightSensor"] = grovePiDevice.getLightSensor()
        if "rotarySensor" in names:
            values["rotarySensor"] = grovePiDevice.getRotarySensor().factor
        if "soundSensor" in names:
            values["soundSensor"] = grovePiDevice.getSoundSensor()
        if "redLed" in names:
            values["redLed"] = grovePiDevice.getRedLed()
        if "blueLed" in names:
            values["blueLed"] = grovePiDevice.getBlueLed()
        if "buzzer" in names:
            values["buzzer"] = grovePiDevice.getBuzzer()
        return values

class ManageSensorActuatorValues(Action):
    def onConfigure(self):
        self.withLabel("Manage the sensor and actuator values").withDescription("Provides management of the sensor and actuator values.")
        self.withArgs([
            NumberType("temperatureSensor").withNullable().withLabel(u"Temperature sensor (°C)").withProvided(ProvidedMeta().withValue().withReadOnly()),
            NumberType("humiditySensor").withNullable().withLabel(u"Humidity sensor (%)").withProvided(ProvidedMeta().withValue().withReadOnly()),
            NumberType("lightSensor").withNullable().withLabel(u"Light sensor").withProvided(ProvidedMeta().withValue().withReadOnly()),
            NumberType("rotarySensor").withNullable().withLabel(u"Rotary sensor").withProvided(ProvidedMeta().withValue().withReadOnly()),
            NumberType("soundSensor").withNullable().withLabel(u"Sound sensor").withProvided(ProvidedMeta().withValue().withReadOnly()),
            BooleanType("redLed").withLabel("Red LED").withProvided(ProvidedMeta().withValue().withOverwrite()),
            IntegerType("blueLed").withMinValue(0).withMaxValue(255).withLabel("Blue LED").withProvided(ProvidedMeta().withValue().withOverwrite()),
            BooleanType("buzzer").withLabel("Buzzer").withProvided(ProvidedMeta().withValue().withOverwrite())
        ]).withNoResult()
        self.withFeature("icon", "thermometer")
    def onCall(self, temperatureSensor, humiditySensor, lightSensor, rotarySensor, soundSensor, redLed, blueLed, buzzer):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        grovePiDevice.setRedLed(redLed)
        grovePiDevice.setBlueLed(blueLed)
        grovePiDevice.setBuzzer(buzzer)
    def onProvideArgs(self, context):
        values = sponge.call("GetSensorActuatorValues", [context.names])
        for name, value in values.iteritems():
            context.provided[name] = ProvidedValue().withValue(value)

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
