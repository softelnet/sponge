"""
Sponge Knowledge base
Demo
"""

class UpperCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256)).displayName("Text to upper case").description("The text that will be converted to upper case.")
        ]
        self.resultMeta = ResultMeta(StringType()).displayName("Upper case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        sponge.getVariable("actionCalled").set(True)
        return text.upper()

class LowerCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to lower case"
        self.description = "Converts a string to lower case."
        self.argsMeta = [ ArgMeta("text", StringType()).displayName("A text that will be changed to lower case") ]
        self.resultMeta = ResultMeta(StringType()).displayName("Lower case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return text.lower()

class ListValues(Action):
    def onConfigure(self):
        self.features = {"visible":False}
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ProvideByAction(Action):
    def onConfigure(self):
        self.displayName = "Action with provided argument"
        self.argsMeta = [ArgMeta("value", StringType()).displayName("Value").provided()]
        self.resultMeta = ResultMeta(StringType()).displayName("Same value")
    def onCall(self, value):
        return value
    def provideArgs(self, names, current):
        return {"value":ArgValue().valueSet(sponge.call("ListValues"))}

