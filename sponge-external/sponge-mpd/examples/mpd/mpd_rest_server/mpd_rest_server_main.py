"""
Sponge Knowledge base
MPD / REST API
"""

def onStartup():
    sponge.logger.info("Starting...")

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArgs([
            ArgMeta("text", StringType()).withLabel("Text to upper case").withDescription("The text that will be converted to upper case."),
            ArgMeta("suffix", AnyType().withNullable(True)).withLabel("Text suffix").withDescription("Not used")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
    def onCall(self, text, optionalText = None):
        self.logger.info("Action {} called", self.meta.name)
        return text.upper() + ( " " + optionalText.upper() if optionalText is not None else "")


