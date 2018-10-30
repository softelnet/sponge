"""
Sponge Knowledge base
Optional action argument
"""

class OptionalArgAction(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("mandatoryText", StringType()), ArgMeta("optionalText", StringType()).optional() ]
        self.resultMeta = ResultMeta(StringType())
    def onCall(self, mandatoryText, optionalText = None):
        return str(mandatoryText) + (str(optionalText) if optionalText is not None else "")
