"""
Sponge Knowledge base
gRPC demo - Counter - viewer.
"""

class ViewCounter(Action):
    def onConfigure(self):
        self.withLabel("Counter").withDescription("Shows the counter.")
        self.withArgs([
            NumberType("counter").withLabel("Counter").withReadOnly().withProvided(ProvidedMeta().withValue()),
        ]).withNonCallable()
        # This action when open in a GUI will subscribe to counterNotification events. When such event arrives, the action arguments
        # will be automatically refreshed, so the counter argument will be read from the variable and provided to a GUI.
        self.withFeatures({"cancelLabel":"Close", "refreshEvents":["counterNotification"]})
    def onProvideArgs(self, context):
        if "counter" in context.provide:
            context.provided["counter"] = ProvidedValue().withValue(sponge.getVariable("counter").get())
