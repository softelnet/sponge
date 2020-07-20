"""
Sponge Knowledge Base
Null handling
"""

class NullHandling(Action):
    def onConfigure(self):
        self.withArgs([
            StringType("arg1").withProvided(ProvidedMeta().withValue()),
        ]).withNoResult()
    def onCall(self, arg1):
        return None
    def onProvideArgs(self, context):
        if "arg1" in context.provide:
            context.provided["arg1"] = ProvidedValue().withValue(None)
