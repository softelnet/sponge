"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided(ArgProvided().value()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgValue().withValue("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided(ArgProvided().value().overwrite()) ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgValue().withValue("PROVIDED")