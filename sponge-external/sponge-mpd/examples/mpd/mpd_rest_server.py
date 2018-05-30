"""
Sponge Knowledge base
MPD / REST API
"""

def onStartup():
    EPS.logger.info("Starting...")

class UpperCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", Type.STRING, True, "Text to upper case", "The text that will be converted to upper case."),
            ArgMeta("suffix", Type.OBJECT, False, "Text suffix", "Not used")]
        self.resultMeta = ResultMeta(Type.STRING, "Upper case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        return str(args[0]).upper() + ( " " + str(args[1]).upper() if (len(args) > 1 and args[1] is not None) else "")

class Reload(Action):
    def onConfigure(self):
        self.displayName = "Reload Sponge knowledge bases"
        self.description = "Reloads Sponge knowledge bases."
        self.argsMeta = []
        self.resultMeta = ResultMeta(Type.VOID)
    def onCall(self, args):
        EPS.reload()

class GetVersion(Action):
    def onConfigure(self):
        self.displayName = "Sponge version"
        self.description = "Shows the version of Sponge."
        self.argsMeta = []
        self.resultMeta = ResultMeta(Type.STRING, "Version", "Sponge version")
    def onCall(self, args):
        return EPS.version
