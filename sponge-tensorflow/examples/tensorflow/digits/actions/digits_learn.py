"""
Sponge Knowledge base
Digits recognition learn library 
"""

class DigitsAddToLearn(Action):
    def onConfigure(self):
        self.displayName = "Add a digit to learn"
        self.description = "Adds a digit to learn"
        self.argsMeta = [
            createImageArgMeta(),
            ArgMeta("digit", StringType()).displayName("Digit").provided(ArgProvided().valueSet())
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, image, digit):
        py4j.facade.addToLearn(image, digit)
        return None
    def onProvideArgs(self, names, current, provided):
        if "digit" in names:
            provided["digit"] = ArgValue().valueSet(py4j.facade.getLabels())

class DigitsLearn(Action):
    def onConfigure(self):
        self.displayName = "Learn a digit"
        self.description = "Learns a digit"
        self.argsMeta = [
            createImageArgMeta(),
            ArgMeta("digit", StringType()).displayName("Digit").provided(ArgProvided().valueSet())
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, image, digit):
        py4j.facade.learn(image, digit)
        return None
    def onProvideArgs(self, names, current, provided):
        if "digit" in names:
            provided["digit"] = ArgValue().valueSet(py4j.facade.getLabels())

class MnistResetModel(Action):
    def onConfigure(self):
        self.displayName = "Reset the model"
        self.description = "Resets the model by loading the state before manual learning"
        self.features = {"confirmation":True}
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        py4j.facade.reset()
        return None
