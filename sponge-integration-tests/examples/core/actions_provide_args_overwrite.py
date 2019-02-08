"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("value", StringType()).withProvided(ArgProvidedMeta().withValue())).withNoResult()
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("value", StringType()).withProvided(ArgProvidedMeta().withValue().withOverwrite())).withNoResult()
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")