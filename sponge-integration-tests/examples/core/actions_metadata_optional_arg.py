"""
Sponge Knowledge base
Optional action argument
"""

class OptionalArgAction(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("mandatoryText", StringType()),
            ArgMeta("optionalText", StringType()).withOptional()
        ])
        self.withResult(ResultMeta(StringType()))
    def onCall(self, mandatoryText, optionalText = None):
        return mandatoryText + (optionalText if optionalText is not None else "")
