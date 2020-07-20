"""
Sponge Knowledge Base
gRPC demo - Notification.
"""

from java.util.concurrent.atomic import AtomicLong
from org.openksavi.sponge.remoteapi.model import RemoteEvent

def onInit():
    sponge.setVariable("notificationNo", AtomicLong(1))

def onBeforeLoad():
    sponge.addType("Person", lambda: RecordType().withFields([
        StringType("firstName").withLabel("First name"),
        StringType("surname").withLabel("Surname")
    ]).withLabel("Person"))

    sponge.addEventType("notification", RecordType().withFields([
        StringType("source").withLabel("Source"),
        IntegerType("severity").withLabel("Severity").withNullable(),
        sponge.getType("Person", "person").withNullable()
    ]).withLabel("Notification").withFeatures({"icon":"alarm-light"}))

class NotificationSender(Trigger):
    def onConfigure(self):
        self.withEvent("notificationSender")
    def onRun(self, event):
        notificationNo = sponge.getVariable("notificationNo").getAndIncrement()
        eventNo = str(notificationNo)

        sponge.event("notification").set({"source":"Sponge", "severity":10, "person":{"firstName":"James", "surname":"Joyce"}}).label(
            "The notification " + eventNo).description("The new event " + eventNo + " notification").feature(
                "icon", IconInfo().withName("alarm-light").withColor("FF0000" if (notificationNo % 2) == 0 else "00FF00")
            ).send()

def onStartup():
    sponge.event("notificationSender").sendEvery(Duration.ofSeconds(10))
