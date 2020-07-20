"""
Sponge Knowledge Base
IoT on Raspberry Pi with a GrovePi board
"""

from org.openksavi.sponge.camel import ScriptRouteBuilder
from java.lang import System
import socket
import sys
import time

class IoTRouteBuilder(ScriptRouteBuilder):
    def createRoute(self, brokerUrl, sensor):
        mqttPublishTopicNamePrefix = sponge.getProperty("mqtt.publishTopicNamePrefix", None)
        credentials = ""
        username = sponge.getProperty("mqtt.username", None)
        if username:
            credentials += "&userName=" + username
        password = sponge.getProperty("mqtt.password", None)
        if password:
            credentials += "&password=" + password

        self.fromS("direct:" + sensor).routeId(sensor)\
            .to("paho:{}?brokerUrl={}&retained=true{}".format(mqttPublishTopicNamePrefix + "/" + sensor, brokerUrl,
                    credentials))
    def configure(self):
        brokerUrl = sponge.getProperty("mqtt.brokerUrl", None)
        if brokerUrl is not None:
            self.createRoute(brokerUrl, "temperature")
            self.createRoute(brokerUrl, "humidity")
            self.createRoute(brokerUrl, "light")

class SensorChangeToMqtt(Trigger):
    def onConfigure(self):
        self.withEvent("sensorChange")
    def onRun(self, event):
        for sensor in event.getAll():
            value = event.get(sensor)
            camel.requestBody("direct:" + sensor, str(value) if value else None)

class TemperatureSensorChange(Correlator):
    def onConfigure(self):
        self.withEvent("sensorChange").withMaxInstances(1)
    def onInit(self):
        self.alarmSentTime = 0
    def onEvent(self, event):
        if event.has("temperature"):
            temperature = event.get("temperature")
            temperatureThreshold = sponge.getProperty("service.temperatureThreshold", None)
            if temperatureThreshold is not None and temperature >= float(temperatureThreshold):
                newTime = System.currentTimeMillis()
                # Convertion to minutes.
                alarmSnoozePeriod = long(sponge.getProperty("service.alarmSnoozePeriod", "0")) * 60000
                if newTime - self.alarmSentTime > alarmSnoozePeriod:
                    sponge.event("temperatureAlarm").set("temperature", temperature).send()
                self.alarmSentTime = newTime

class TemperatureAlarmListener(Trigger):
    def onConfigure(self):
        self.withEvent("temperatureAlarm")
    def onRun(self, event):
        temperature = event.get("temperature")
        self.logger.warn("Temperature alarm: {}!", temperature)
        sponge.call("SendNotificationSms", [u"The temperature {:.2f}°C is too high".format(temperature)])


class StartNotificationTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("notificationStart")
    def onRun(self, event):
        serviceName = sponge.getProperty("service.name", "Service")
        try:
            sponge.call("SendNotificationSms", ["{} started".format(serviceName)])
        except:
            sponge.logger.warn("SendNotificationSms error: {}", sys.exc_info()[1])

        try:
            sponge.call("SendNotificationEmail", ["{} started".format(serviceName), "Host: {}".format(socket.gethostname()),
                    [sponge.call("TakePictureAsFile")]])
        except:
            sponge.logger.warn("SendNotificationEmail error: {}", sys.exc_info()[1])

def onStartup():
    sponge.call("SetLcd", ["Sponge starting...", "006030"])

    # Manual start of the Remote API (autoStart is turned off) because the Remote API server must start after the Camel context has started.
    camel.waitForContextFullyStarted()
    camel.context.addRoutes(IoTRouteBuilder())
    remoteApiServer.start()
    grpcApiServer.start()

    sponge.call("SetLcd", ["Sponge started", "00f767"])

    # Sent start notification event.
    sponge.event("notificationStart").send()

    # Start sensor polling event chains.
    sponge.event("dhtSensorListener").send()
    sponge.event("rotarySensorListener").send()
    sponge.event("lightSensorListener").send()

def onShutdown():
     try:
         sponge.call("SetLcd", ["", "000000"])
     except:
         sponge.logger.warn("Shutdown error: {}", sys.exc_info()[1])

def onAfterReload():
    pass
