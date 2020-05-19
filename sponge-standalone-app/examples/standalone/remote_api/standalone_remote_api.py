"""
Sponge Knowledge base
Remote API
"""

class UpperCase(Action):
    def onConfigure(self):
        self.withArg(StringType("text").withLabel("Text to upper case")).withResult(StringType().withLabel("Upper case text"))
    def onCall(self, text):
        self.logger.info("Action {} called", self.meta.name)
        return text.upper()

def onStartup():
    # Manual start of the Remote API (autoStart is turned off) because the Remote API server must start after the Camel context has started.
    remoteApiServer.start()