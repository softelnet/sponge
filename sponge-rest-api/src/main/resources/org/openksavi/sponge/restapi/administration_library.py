"""
Sponge Knowledge base
Administration library.
"""

class AdministrationReload(Action):
    def onConfigure(self):
        self.displayName = "Reload Sponge knowledge bases"
        self.description = "Reloads Sponge knowledge bases."
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        EPS.reload()

class AdministrationGetVersion(Action):
    def onConfigure(self):
        self.displayName = "Sponge version"
        self.description = "Shows the Sponge version."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType()).displayName("Version").description("Sponge version")
    def onCall(self):
        return EPS.version

