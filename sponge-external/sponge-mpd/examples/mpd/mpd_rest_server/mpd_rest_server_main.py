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
        self.meta = {"status":"test"}
        self.argsMeta = [
            ArgMeta("text", Type.STRING).displayName("Text to upper case").description("The text that will be converted to upper case."),
            ArgMeta("suffix", Type.OBJECT).required(False).displayName("Text suffix").description("Not used")]
        self.resultMeta = ResultMeta(Type.STRING).displayName("Upper case text")
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        return str(args[0]).upper() + ( " " + str(args[1]).upper() if (len(args) > 1 and args[1] is not None) else "")


