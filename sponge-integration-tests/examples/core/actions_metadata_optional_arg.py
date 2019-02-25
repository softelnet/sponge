"""
Sponge Knowledge base
Optional action argument
"""

class OptionalArgAction(Action):
    def onConfigure(self):
        self.withArgs([
            StringType("mandatoryText"),
            StringType("optionalText").withOptional()
        ])
        self.withResult(StringType())
    def onCall(self, mandatoryText, optionalText = None):
        return mandatoryText + (optionalText if optionalText is not None else "")
