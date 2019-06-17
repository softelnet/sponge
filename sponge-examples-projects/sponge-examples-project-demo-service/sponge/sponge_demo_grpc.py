"""
Sponge Knowledge base
gRPC demo.
"""
from java.util.concurrent.atomic import AtomicLong

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
    ]).withLabel("Notification"))

class NotificationSender(Trigger):
    def onConfigure(self):
        self.withEvent("notificationSender")
    def onRun(self, event):
        eventNo = str(sponge.getVariable("notificationNo").getAndIncrement())
        sponge.event("notification").set({"source":"Sponge", "severity":10, "person":{"firstName":"James", "surname":"Joyce"}}).label(
            "The notification " + eventNo).description("The new event " + eventNo + " notification").send()

def onStartup():
    # Enable support processors in this knowledge base.
    grpcApiServer.enableSupport(sponge)

    sponge.event("notificationSender").sendEvery(Duration.ofSeconds(10))
