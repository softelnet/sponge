"""
Sponge Knowledge base
Action call
"""

class UpperCase(Action):
    def onConfigure(self):
        self.withLabel("Convert to upper case").withDescription("Converts a string to upper case.")
        self.withArg(
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ).withResult(StringType().withLabel("Upper case text"))
    def onCall(self, text):
        return text.upper()

class ActionDelegateWithNamedArgs(Action):
    def onConfigure(self):
        self.withArgs([
            StringType("actionName"),
            MapType("actionArgs").withKey(StringType()).withValue(AnyType())
        ]).withResult(AnyType())
    def onCall(self, actionName, actionArgs):
        return sponge.call(actionName, actionArgs)

class UpperCaseNoMeta(Action):
    def onCall(self, text):
        return text.upper()