"""
Sponge Knowledge base
Engine - administration library.
"""

class EngineReload(Action):
    def onConfigure(self):
        self.withLabel("Reload Sponge knowledge bases").withDescription("Reloads Sponge knowledge bases.").withFeatures({"confirmation":True})
        self.withNoArgs().withNoResult()
    def onCall(self):
        sponge.reload()
