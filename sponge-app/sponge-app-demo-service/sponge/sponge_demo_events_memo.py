"""
Sponge Knowledge base
gRPC demo - Memo.
"""
from org.openksavi.sponge.remoteapi.model import RemoteEvent

def onBeforeLoad():
    sponge.addEventType("memo", RecordType().withFields([
        StringType("message").withLabel("Message"),
    ]).withLabel("Memo").withFeatures({"handlerAction":"ViewMemoEvent", "icon":"note-text-outline"}))

class ViewMemoEvent(Action):
    def onConfigure(self):
        self.withLabel("Memo").withDescription("Shows the memo event.")
        self.withArgs([
            ObjectType("event", RemoteEvent).withFeature("visible", False),
            StringType("uppercaseMessage").withLabel("Upper case message").withReadOnly().withProvided(
                    ProvidedMeta().withValue().withDependency("event")),
        ])
        self.withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Dismiss", "cancelLabel":"Close"})
    def onCall(self, event, uppercaseMessage):
        pass
    def onProvideArgs(self, context):
        if "uppercaseMessage" in context.provide:
            message = context.current["event"].attributes["message"]
            context.provided["uppercaseMessage"] = ProvidedValue().withValue(message.upper() if message else "NO MESSAGE")
