"""
Sponge Knowledge Base
Remote API
"""

class UpperCase(Action):
    def onConfigure(self):
        self.withArg(StringType("text").withLabel("Text to upper case")).withResult(StringType().withLabel("Upper case text"))
    def onCall(self, text):
        self.logger.info("Action {} called", self.meta.name)
        return text.upper()
