"""
Sponge Knowledge base
MPD / REST API
"""

def onStartup():
    sponge.logger.info("Starting...")

class UpperCase(Action):
    def onConfigure(self):
        self.label = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", StringType()).withLabel("Text to upper case").withDescription("The text that will be converted to upper case."),
            ArgMeta("suffix", AnyType().withNullable(True)).withLabel("Text suffix").withDescription("Not used")
        ]
        self.resultMeta = ResultMeta(StringType()).withLabel("Upper case text")
    def onCall(self, text, optionalText = None):
        self.logger.info("Action {} called", self.name)
        return text.upper() + ( " " + optionalText.upper() if optionalText is not None else "")


