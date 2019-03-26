"""
Sponge Knowledge base
Service
"""

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArg(
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ).withResult(StringType().withLabel("Upper case text"))
        self.withFeature("icon", "format-letter-case-upper")
    def onCall(self, text):
        return text.upper()
