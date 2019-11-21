"""
Sponge Knowledge base
Engine - administration library.
"""

class EngineReload(Action):
    def onConfigure(self):
        self.withLabel("Reload Sponge knowledge bases").withDescription("Reloads Sponge knowledge bases.").withFeatures({
            "intent":"reload", "confirmation":True})
        self.withNoArgs().withNoResult()
        self.withFeature("icon", "server")
    def onCall(self):
        sponge.reload()
