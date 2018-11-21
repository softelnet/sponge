"""
Sponge Knowledge base
Engine - public library.
"""

class EngineGetVersion(Action):
    def onConfigure(self):
        self.displayName = "Sponge version"
        self.description = "Shows the Sponge version."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType()).displayName("Version").description("Sponge version")
    def onCall(self):
        return sponge.version

