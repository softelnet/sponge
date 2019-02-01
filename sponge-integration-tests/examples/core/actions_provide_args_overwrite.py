"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided(ArgProvidedMeta().value()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided(ArgProvidedMeta().value().overwrite()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgProvidedValue().withValue("PROVIDED")