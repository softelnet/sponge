"""
Sponge Knowledge base
REST API
"""

class UpperCase(Action):
    def onConfigure(self):
        self.argsMeta = [ArgMeta("text", StringType()).label("Text to upper case")]
        self.resultMeta = ResultMeta(StringType()).label("Upper case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return text.upper()

def onStartup():
    # Manual start of the REST API (autoStart is turned off) because the REST API server must start after the Camel context has started.
    restApiServer.start()