"""
Sponge Knowledge base
Engine - public library.
"""

class EngineGetVersion(Action):
    def onConfigure(self):
        self.withLabel("Sponge version").withDescription("Shows the Sponge version.")
        self.withNoArgs().withResult(StringType().withLabel("Version").withDescription("Sponge version"))
    def onCall(self):
        return sponge.version

