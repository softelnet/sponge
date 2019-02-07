"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).withProvided(ArgProvidedMeta().withValue()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).withProvided(ArgProvidedMeta().withValue().withOverwrite()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")