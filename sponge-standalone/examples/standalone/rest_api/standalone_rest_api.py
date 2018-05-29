"""
Sponge Knowledge base
REST API
"""

class UpperCase(Action):
    def onConfigure(self):
        self.argsMeta = [ArgMeta("text", Type.STRING, True, "Text to upper case")]
        self.resultMeta = ResultMeta(Type.STRING, "Upper case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        return str(args[0]).upper()

def onStartup():
    # Manual start of the REST API (autoStart is turned off) because the Camel context must be started before the REST API server.
    restApi.start()