"""
Sponge Knowledge base
Test
"""

class UpperCase(Action):
    def onConfigure(self):
        self.displayName = "Convert to upper case"
        self.description = "Converts a string to upper case."
        self.argsMeta = [
            ArgMeta("text", StringType()).displayName("Text to upper case").description("The text that will be converted to upper case.")
        ]
        self.resultMeta = ResultMeta(StringType()).displayName("Upper case text")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
        return str(text).upper()

class EchoImage(Action):
    def onConfigure(self):
        self.displayName = "Echo an image"
        self.argsMeta = [ArgMeta("image", BinaryType().mimeType("image/png")).displayName("Image")]
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/png")).displayName("Image echo")
    def onCall(self, image):
        return image

class ListValues(Action):
    def onConfigure(self):
        self.features = {"visible":False}
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType()))
    def onCall(self):
        return ["value1", "value2", "value3"]

class ActionTypeAction(Action):
    def onConfigure(self):
        self.displayName = "Action type use case"
        self.argsMeta = [ArgMeta("value", ActionType("ListValues")).displayName("Value")]
        self.resultMeta = ResultMeta(StringType()).displayName("Same value")
    def onCall(self, value):
        return value