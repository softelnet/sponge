"""
Sponge Knowledge base
MNIST administration library 
"""

class MnistLearn(Action):
    def onConfigure(self):
        self.displayName = "Learn a digit"
        self.description = "Manually learns the model to recognize a digit"
        self.argsMeta = [createImageArgMeta(), ArgMeta("digit", IntegerType()).displayName("Digit")]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, image, digit):
        py4j.facade.learn(image, digit)
        return None

class MnistResetModel(Action):
    def onConfigure(self):
        self.displayName = "Reset the model"
        self.description = "Resets the model by loading the state before manual learning"
        self.argsMeta = []
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self):
        py4j.facade.reset()
        return None