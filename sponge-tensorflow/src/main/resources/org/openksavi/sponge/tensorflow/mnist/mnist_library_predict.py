"""
Sponge Knowledge base
MNIST library
"""

class MnistPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.description = "Tries to recognize a handwritten digit"
        self.argsMeta = [createImageArgMeta()]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        predictions = py4j.facade.predict(image)
        prediction = predictions.index(max(predictions))
        probability = predictions[prediction]

        # Handle the optional predictionThreshold Sponge variable.
        predictionThreshold = sponge.getVariable("predictionThreshold", None)
        if predictionThreshold and probability < float(predictionThreshold):
            self.logger.debug("The prediction {} probability {} is lower than the threshold {}.", prediction, probability, predictionThreshold)
            return None
        else:
            self.logger.debug("Prediction: {}, probability: {}", prediction, probability)
            return prediction

class MnistPredictDetailed(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit (probabilities)"
        self.description = "Tries to recognize a handwritten digit returning a probabilities list"
        self.argsMeta = [createImageArgMeta()]
        self.resultMeta = ResultMeta(ListType(NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        return py4j.facade.predict(image)

def mnistServiceInit(py4jPlugin):
    SpongeUtils.awaitUntil(lambda: py4jPlugin.facade.isReady())