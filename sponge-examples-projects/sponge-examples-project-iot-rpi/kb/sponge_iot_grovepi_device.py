"""
Sponge Knowledge Base
GrovePi device
"""

from java.lang import Double
from org.iot.raspberry.grovepi.devices import GroveLightSensor, GroveTemperatureAndHumiditySensor, GroveRotarySensor, GroveLed, GroveSoundSensor

class GrovePiDevice:
    """ Connect:
        - the DHT sensor to port D2,
        - the light sensor to port A1,
        - the rotary sensor to port A0,
        - the sound sensor to port A2,
        - the red led to port D4,
        - the blue led to port D5,
        - the buzzer to port D7.
    """
    def __init__(self):
        self.__temperatureHumiditySensor = grovepi.get(lambda: GroveTemperatureAndHumiditySensor(grovepi.device, 2,
                                                                           GroveTemperatureAndHumiditySensor.Type.DHT11))
        self.__lightSensor = grovepi.get(lambda: GroveLightSensor(grovepi.device, 1))
        self.__rotarySensor = grovepi.get(lambda: GroveRotarySensor(grovepi.device, 0))
        self.__soundSensor = grovepi.get(lambda: GroveSoundSensor(grovepi.device, 2))

        self.__redLed = grovepi.get(lambda: grovepi.device.getDigitalOut(4))
        self.__redLedValue = False

        self.__blueLed = grovepi.get(lambda: GroveLed(grovepi.device, 5))
        self.__blueLedValue = 0

        self.__buzzer = grovepi.get(lambda: grovepi.device.getDigitalOut(7))
        self.__buzzerValue = False

        self.__lcd = grovepi.get(lambda: grovepi.device.LCD)
        self.__lcdText = ""
        self.__lcdColor = "000000"

    def getTemperatureHumiditySensor(self):
        result = grovepi.get(lambda: self.__temperatureHumiditySensor.get())
        if Double.isNaN(result.temperature) or Double.isNaN(result.humidity):
            # Read error.
            return None
        return result

    def getLightSensor(self):
        return grovepi.get(lambda: self.__lightSensor.get())

    def getRotarySensor(self):
        return grovepi.get(lambda: self.__rotarySensor.get())

    def getSoundSensor(self):
        return grovepi.get(lambda: self.__soundSensor.get())

    def setRedLed(self, value):
        grovepi.set(lambda: self.__redLed.set(value))
        self.__redLedValue = value

    def getRedLed(self):
        return self.__redLedValue

    def setBlueLed(self, value):
        grovepi.set(lambda: self.__blueLed.set(value))
        self.__blueLedValue = value

    def getBlueLed(self):
        return self.__blueLedValue

    def setBuzzer(self, value):
        grovepi.set(lambda: self.__buzzer.set(value))
        self.__buzzerValue = value

    def getBuzzer(self):
        return self.__buzzerValue

    def setLcd(self, text, color):
        def doSetLcd():
            if color:
                rgb = tuple(int(color[i:i+2], 16) for i in (0, 2 ,4))
                self.__lcd.setRGB(rgb[0], rgb[1], rgb[2])
                self.__lcdColor = color
            newText = text if text is not None else self.__lcdText
            self.__lcd.text = newText
            self.__lcdText = newText
        grovepi.set(doSetLcd)
        sponge.event("lcdChange").set("text", self.__lcdText).send()

    def getLcdText(self):
        return self.__lcdText

    def getLcdColor(self):
        return self.__lcdColor
