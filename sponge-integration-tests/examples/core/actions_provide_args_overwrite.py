"""
Sponge Knowledge base
Provide arguments with overwrite
"""

class ProvideArgNoOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided() ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgValue().value("PROVIDED")

class ProvideArgOverwrite(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("value", StringType()).provided().overwrite() ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, value):
        return
    def onProvideArgs(self, names, current, provided):
        if "value" in names:
            provided["value"] = ArgValue().value("PROVIDED")