"""
Sponge Knowledge base
gRPC demo - Counter - event handler.
"""

from org.openksavi.sponge.restapi.model import RemoteEvent

def onBeforeLoad():
    # Define an event type with a handler action. This event type won't be shown in a list of events to subscribe in a GUI.
    sponge.addEventType("counterNotification", RecordType().withFields([
        NumberType("counter").withLabel("Counter"),
    ]).withLabel("Counter").withFeatures({"visible":False, "handlerAction":"ViewCounterEvent"}))

class ViewCounterEvent(Action):
    def onConfigure(self):
        self.withLabel("Counter event").withDescription("Shows the counter event.")
        self.withArgs([
            # Define a required event argument. It won't be shown in a GUI.
            ObjectType("event", RemoteEvent).withFeature("visible", False),
            # The counter value associated with an event (not necessarily a current value) will be shown in a GUI.
            NumberType("counter").withLabel("Counter").withProvided(
                    ProvidedMeta().withValue().withReadOnly().withDependency("event")),
        ]).withNoResult()
        # The action will not be shown in an action list in a GUI.
        self.withFeatures({"visible":False, "callLabel":"Dismiss", "cancelLabel":"Close"})
    def onCall(self, event, counter):
        # Here some additional processing can be implemented on the server side on dismissing an event from an action.
        pass
    def onProvideArgs(self, context):
        if "counter" in context.provide:
            # Provide a value of an event attribute, i.e. copy an event attribute value to a provided action argument.
            # Note that this logic, although simple, is performed on the server side accoring to Sponge concepts.
            context.provided["counter"] = ProvidedValue().withValue(context.current["event"].attributes["counter"])
