"""
Sponge Knowledge base
Digits recognition learn library 
"""

class DigitsAddToLearn(Action):
    def onConfigure(self):
        self.withLabel("Add a digit to learn").withDescription("Adds a digit to learn")
        self.withArgs([
            createImageArgMeta(),
            ArgMeta("digit", StringType()).withLabel("Digit").withProvided(ArgProvidedMeta().withValueSet())
        ]).withNoResult()
    def onCall(self, image, digit):
        py4j.facade.addToLearn(image, digit)
        return None
    def onProvideArgs(self, context):
        if "digit" in context.names:
            context.provided["digit"] = ArgProvidedValue().withValueSet(py4j.facade.getLabels())

class DigitsLearn(Action):
    def onConfigure(self):
        self.withLabel("Learn a digit").withDescription("Learns a digit")
        self.withArgs([
            createImageArgMeta(),
            ArgMeta("digit", StringType()).withLabel("Digit").withProvided(ArgProvidedMeta().withValueSet())
        ]).withNoResult()
    def onCall(self, image, digit):
        py4j.facade.learn(image, digit)
        return None
    def onProvideArgs(self, context):
        if "digit" in context.names:
            context.provided["digit"] = ArgProvidedValue().withValueSet(py4j.facade.getLabels())

class MnistResetModel(Action):
    def onConfigure(self):
        self.withLabel("Reset the model").withDescription("Resets the model by loading the state before manual learning").withFeatures({"confirmation":True})
        self.withNoArgs().withNoResult()
    def onCall(self):
        py4j.facade.reset()
        return None
