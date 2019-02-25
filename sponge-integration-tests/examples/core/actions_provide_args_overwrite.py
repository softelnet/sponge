"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.withArg(StringType("value").withProvided(ProvidedMeta().withValue())).withNoResult()
    def onCall(self, value):
        return
    def onProvideArgs(self, context):
        if "value" in context.names:
            context.provided["value"] = ProvidedValue().withValue("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.withArg(StringType("value").withProvided(ProvidedMeta().withValue().withOverwrite())).withNoResult()
    def onCall(self, value):
        return
    def onProvideArgs(self, context):
        if "value" in context.names:
            context.provided["value"] = ProvidedValue().withValue("PROVIDED")