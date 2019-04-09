"""
Sponge Knowledge base
Digits recognition library
"""

class DigitsPredictProbabilities(Action):
    def onConfigure(self):
        self.withLabel("Recognize a digit (probabilities)").withDescription("Recognizes a handwritten digit returning probabilities")
        self.withArg(createImageType("image")).withResult(MapType(StringType(), NumberType()).withLabel("Digit probabilities"))
        self.withFeature("icon", "brain")
    def onCall(self, image):
        return py4j.facade.predict(image)
