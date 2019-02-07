"""
Sponge Knowledge base
Digits recognition learn library 
"""

class DigitsAddToLearn(Action):
    def onConfigure(self):
        self.label = "Add a digit to learn"
        self.description = "Adds a digit to learn"
        self.argsMeta = [
            createImageArgMeta(),
            ArgMeta("digit", StringType()).withLabel("Digit").withProvided(ArgProvidedMeta().withValueSet())
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, image, digit):
        py4j.facade.addToLearn(image, digit)
        return None
    def onProvideArgs(self, names, current, provided):
        if "digit" in names:
            provided["digit"] = ArgProvidedValue().withValueSet(py4j.facade.getLabels())

class DigitsLearn(Action):
    def onConfigure(self):
        self.label = "Learn a digit"
        self.description = "Learns a digit"
        self.argsMeta = [
            createImageArgMeta(),
            ArgMeta("digit", StringType()).withLabel("Digit").withProvided(ArgProvidedMeta().withValueSet())
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, image, digit):
        py4j.facade.learn(image, digit)
        return None
    def onProvideArgs(self, names, current, provided):
        if "digit" in names:
            provided["digit"] = ArgProvidedValue().withValueSet(py4j.facade.getLabels())

class MnistResetModel(Action):
    def onConfigure(self):
        self.label = "Reset the model"
        self.description = "Resets the model by loading the state before manual learning"
        self.features = {"confirmation":True}
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        py4j.facade.reset()
        return None
