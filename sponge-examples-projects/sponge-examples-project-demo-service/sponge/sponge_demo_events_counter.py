"""
Sponge Knowledge base
gRPC demo - Counter.
"""
from java.util.concurrent.atomic import AtomicLong
from org.openksavi.sponge.restapi.model import RemoteEvent

def onInit():
    sponge.setVariable("counter", AtomicLong(1))

def onBeforeLoad():
    sponge.addEventType("counterNotification", RecordType().withFields([
        NumberType("counter").withLabel("Counter"),
    ]).withLabel("Counter").withFeatures({"visible":False, "handlerAction":"ViewCounterEvent"}))

class CounterSender(Trigger):
    def onConfigure(self):
        self.withEvent("counterSender")
    def onRun(self, event):
        value = sponge.getVariable("counter").incrementAndGet()
        sponge.event("counterNotification").set({"counter":value}).label("The counter is " + str(value)).send()

class ViewCounterEvent(Action):
    def onConfigure(self):
        self.withLabel("Counter event").withDescription("Shows the counter event.")
        self.withArgs([
            ObjectType("event", RemoteEvent).withFeature("visible", False),
            NumberType("counter").withLabel("Counter").withProvided(
                    ProvidedMeta().withValue().withReadOnly().withDependency("event")),
        ])
        self.withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Dismiss", "refreshLabel":None, "clearLabel":None, "cancelLabel":"Close"})
    def onCall(self, event, counter):
        pass
    def onProvideArgs(self, context):
        if "counter" in context.names:
            context.provided["counter"] = ProvidedValue().withValue(context.current["event"].attributes["counter"])

class ViewCounter(Action):
    def onConfigure(self):
        self.withLabel("Counter").withDescription("Shows the counter.")
        self.withArgs([
            NumberType("counter").withLabel("Counter").withProvided(ProvidedMeta().withValue().withReadOnly()),
        ])
        self.withCallable(False)
        self.withFeatures({"callLabel":"Dismiss", "refreshLabel":None, "clearLabel":None, "cancelLabel":"Close", "refreshEvents":["counterNotification"]})
    def onProvideArgs(self, context):
        if "counter" in context.names:
            context.provided["counter"] = ProvidedValue().withValue(sponge.getVariable("counter").get())

def onStartup():
    sponge.event("counterSender").sendEvery(Duration.ofSeconds(5))
