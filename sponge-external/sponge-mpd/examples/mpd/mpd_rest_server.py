"""
Sponge Knowledge base
MPD / REST API
"""

def onStartup():
    EPS.logger.info("Starting...")

class UpperCase(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("text", Type.STRING, True, "Text to upper case"),
            ArgMeta("suffix", Type.OBJECT, False, "Text suffix")]
        self.resultMeta = ResultMeta(Type.STRING, "Upper case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        return str(args[0]).upper() + ( " " + str(args[1]).upper() if (len(args) > 1 and args[1] is not None) else "")

class Reload(Action):
    def onConfigure(self):
        self.displayName = "Reload Sponge knowledge bases"
        self.argsMeta = []
        self.resultMeta = ResultMeta(Type.VOID)
    def onCall(self, args):
        EPS.reload()

class GetVersion(Action):
    def onConfigure(self):
        self.displayName = "Sponge version"
        self.argsMeta = []
        self.resultMeta = ResultMeta(Type.STRING, "Version")
    def onCall(self, args):
        return EPS.version
