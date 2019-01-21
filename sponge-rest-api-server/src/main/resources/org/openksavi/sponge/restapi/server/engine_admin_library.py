"""
Sponge Knowledge base
Engine - administration library.
"""

class EngineReload(Action):
    def onConfigure(self):
        self.label = "Reload Sponge knowledge bases"
        self.description = "Reloads Sponge knowledge bases."
        self.features = {"confirmation":True}
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        sponge.reload()
