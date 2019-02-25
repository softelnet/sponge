"""
Sponge Knowledge base
Action configuration using builder-style methods
"""

class UpperEchoAction(Action):
    def onConfigure(self):
        self.withLabel("Echo Action").withDescription("Returns the upper case string").withArg(
            StringType("text").withLabel("Argument 1").withDescription("Argument 1 description")
        ).withResult(StringType().withLabel("Upper case string").withDescription("Result description"))
    def onCall(self, text):
    	return self.meta.label + " returns: " + text.upper()

class UpperEchoChangedNameAction(Action):
    def onConfigure(self):
        self.withName("UpperAction").withLabel("Echo Action").withDescription("Returns the upper case string").withArg(
            StringType("text").withLabel("Argument 1").withDescription("Argument 1 description")
        ).withResult(StringType().withLabel("Upper case string").withDescription("Result description"))
    def onCall(self, text):
        return self.meta.label + " returns: " + text.upper()
