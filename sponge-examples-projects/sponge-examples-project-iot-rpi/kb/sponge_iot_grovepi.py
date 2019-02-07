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
        self.label = "Set the GrovePi mode"
        self.description = "Sets the GrovePi mode."
        self.argsMeta = [ArgMeta("mode", StringType()).withLabel("The GrovePi mode").withProvided(ArgProvidedMeta().withValue().withValueSet().withOverwrite())]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, mode):
        if mode not in ["auto", "manual"]:
            raise Exception("Unsupported GrovePi mode: " + mode)
        sponge.setVariable("grovePiMode", mode)
    def onProvideArgs(self, names, current, provided):
        if "mode" in names:
            provided["mode"] = ArgProvidedValue().withValue(sponge.getVariable("grovePiMode", None)).withAnnotatedValueSet([AnnotatedValue("auto").witLabel("Auto"),
                                                                                                          AnnotatedValue("manual").witLabel("Manual")])

class ManageLcd(Action):
    def onConfigure(self):
        self.label = "Manage the LCD text and color"
        self.description = "Provides management of the LCD properties (display text and color). A null value doesn't change an LCD property."
        self.argsMeta = [
            ArgMeta("currentText", StringType().withMaxLength(256).withNullable(True).withFeatures({"maxLines":2}))
                .withLabel("Current LCD text").withDescription("The currently displayed LCD text.").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256).withNullable(True).withFeatures({"maxLines":2}))
                .withLabel("Text to display").withDescription("The text that will be displayed in the LCD.").withProvided(ArgProvidedMeta().withValue()),
            ArgMeta("color", StringType().withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"}))
                .withLabel("LCD color").withDescription("The LCD color.").withProvided(ArgProvidedMeta().withValue().withOverwrite()),
            ArgMeta("clearText", BooleanType().withNullable(True).withDefaultValue(False))
                .withLabel("Clear text").withDescription("The text the LCD will be cleared.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, currentText, text, color, clearText = None):
        sponge.call("SetLcd", [text, color, clearText])
    def onProvideArgs(self, names, current, provided):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        if "currentText" in names:
            provided["currentText"] = ArgProvidedValue().withValue(grovePiDevice.getLcdText())
        if "text" in names:
            provided["text"] = ArgProvidedValue().withValue(grovePiDevice.getLcdText())
        if "color" in names:
            provided["color"] = ArgProvidedValue().withValue(grovePiDevice.getLcdColor())

class SetLcd(Action):
    def onCall(self, text, color, clearText = None):
        sponge.getVariable("grovePiDevice").setLcd("" if (clearText or text is None) else text, color)

class GetLcdText(Action):
    def onConfigure(self):
        self.label = "Get the LCD text"
        self.description = "Returns the LCD text."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().withFeatures({"maxLines":5})).withLabel("LCD Text")
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
        self.label = "Manage the sensor and actuator values"
        self.description = "Provides management of the sensor and actuator values."
        self.argsMeta = [
            ArgMeta("temperatureSensor", NumberType().withNullable()).withLabel(u"Temperature sensor (°C)").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("humiditySensor", NumberType().withNullable()).withLabel(u"Humidity sensor (%)").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("lightSensor", NumberType().withNullable()).withLabel(u"Light sensor").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("rotarySensor", NumberType().withNullable()).withLabel(u"Rotary sensor").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("soundSensor", NumberType().withNullable()).withLabel(u"Sound sensor").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("redLed", BooleanType()).withLabel("Red LED").withProvided(ArgProvidedMeta().withValue().withOverwrite()),
            ArgMeta("blueLed", IntegerType().withMinValue(0).withMaxValue(255)).withLabel("Blue LED").withProvided(ArgProvidedMeta().withValue().withOverwrite()),
            ArgMeta("buzzer", BooleanType()).withLabel("Buzzer").withProvided(ArgProvidedMeta().withValue().withOverwrite())
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, temperatureSensor, humiditySensor, lightSensor, rotarySensor, soundSensor, redLed, blueLed, buzzer):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        grovePiDevice.setRedLed(redLed)
        grovePiDevice.setBlueLed(blueLed)
        grovePiDevice.setBuzzer(buzzer)
    def onProvideArgs(self, names, current, provided):
        values = sponge.call("GetSensorActuatorValues", [names])
        for name, value in values.iteritems():
            provided[name] = ArgProvidedValue().withValue(value)

class DhtSensorListener(Correlator):
    def onConfigure(self):
        self.event = "dhtSensorListener"
        self.maxInstances = 1
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
        self.event = "rotarySensorListener"

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
        self.event = "lightSensorListener"
        self.maxInstances = 1
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
