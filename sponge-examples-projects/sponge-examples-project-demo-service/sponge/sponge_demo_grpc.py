"""
Sponge Knowledge base
gRPC demo.
"""
from java.util.concurrent.atomic import AtomicLong
from org.openksavi.sponge.restapi.model import RemoteEvent

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

    sponge.addEventType("memo", RecordType().withFields([
        StringType("message").withLabel("Message"),
    ]).withLabel("Memo").withFeature("handlerAction", "ShowMemoEvent"))

class NotificationSender(Trigger):
    def onConfigure(self):
        self.withEvent("notificationSender")
    def onRun(self, event):
        eventNo = str(sponge.getVariable("notificationNo").getAndIncrement())
        sponge.event("notification").set({"source":"Sponge", "severity":10, "person":{"firstName":"James", "surname":"Joyce"}}).label(
            "The notification " + eventNo).description("The new event " + eventNo + " notification").send()

class ShowMemoEvent(Action):
    def onConfigure(self):
        self.withLabel("Memo").withDescription("Shows the memo event.")
        self.withArgs([
            ObjectType("event", RemoteEvent).withFeature("visible", False),
            StringType("uppercaseMessage").withLabel("Upper case message").withProvided(
                    ProvidedMeta().withValue().withDependency("event")),
        ])
        self.withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Dismiss", "refreshLabel":None, "clearLabel":None, "cancelLabel":"Close"})
    def onCall(self, event, uppercaseMessage):
        pass
    def onProvideArgs(self, context):
        if "uppercaseMessage" in context.names:
            message = context.current["event"].attributes["message"]
            context.provided["uppercaseMessage"] = ProvidedValue().withValue(message.upper() if message else "NO MESSAGE")

def onStartup():
    # Enable support actions in this knowledge base.
    grpcApiServer.enableSupport(sponge)

    sponge.event("notificationSender").sendEvery(Duration.ofSeconds(10))
