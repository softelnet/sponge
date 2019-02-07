"""
Sponge Knowledge base
Engine - public library.
"""

class EngineGetVersion(Action):
    def onConfigure(self):
        self.label = "Sponge version"
        self.description = "Shows the Sponge version."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType()).withLabel("Version").withDescription("Sponge version")
    def onCall(self):
        return sponge.version

