"""
Sponge Knowledge base
gRPC demo - Memo.
"""
from org.openksavi.sponge.restapi.model import RemoteEvent

def onBeforeLoad():
    sponge.addEventType("memo", RecordType().withFields([
        StringType("message").withLabel("Message"),
    ]).withLabel("Memo").withFeature("handlerAction", "ViewMemoEvent"))

class ViewMemoEvent(Action):
    def onConfigure(self):
        self.withLabel("Memo").withDescription("Shows the memo event.")
        self.withArgs([
            ObjectType("event", RemoteEvent).withFeature("visible", False),
            StringType("uppercaseMessage").withLabel("Upper case message").withProvided(
                    ProvidedMeta().withValue().withReadOnly().withDependency("event")),
        ])
        self.withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Dismiss", "cancelLabel":"Close"})
    def onCall(self, event, uppercaseMessage):
        pass
    def onProvideArgs(self, context):
        if "uppercaseMessage" in context.provide:
            message = context.current["event"].attributes["message"]
            context.provided["uppercaseMessage"] = ProvidedValue().withValue(message.upper() if message else "NO MESSAGE")
