"""
Sponge Knowledge Base
Engine - public library.
"""

class EngineGetVersion(Action):
    def onConfigure(self):
        self.withLabel("Sponge version").withDescription("Shows the Sponge version.")
        self.withNoArgs().withResult(StringType().withLabel("Version").withDescription("Sponge version"))
        self.withFeature("icon", "server")
    def onCall(self):
        return sponge.version

