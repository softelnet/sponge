"""
Sponge Knowledge base
Digits recognition library
"""

class DigitsPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.description = "Recognizes a handwritten digit"
        self.argsMeta = [createImageArgMeta()]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        predictions = py4j.facade.predict(image)
        prediction = max(predictions, key=predictions.get)
        probability = predictions[prediction]

        # Handle the optional predictionThreshold Sponge variable.
        predictionThreshold = sponge.getVariable("predictionThreshold", None)
        if predictionThreshold and probability < float(predictionThreshold):
            self.logger.debug("The prediction {} probability {} is lower than the threshold {}.", prediction, probability, predictionThreshold)
            return None
        else:
            self.logger.debug("Prediction: {}, probability: {}", prediction, probability)
            return int(prediction)

class DigitsPredictProbabilities(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit (probabilities)"
        self.description = "Recognizes a handwritten digit returning probabilities"
        self.argsMeta = [createImageArgMeta()]
        self.resultMeta = ResultMeta(MapType(StringType(), NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        return py4j.facade.predict(image)

def imageClassifierServiceInit(py4jPlugin):
    SpongeUtils.awaitUntil(lambda: py4jPlugin.facade.isReady())
