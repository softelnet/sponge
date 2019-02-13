"""
Sponge Knowledge base
Digits recognition library
"""

class DigitsPredict(Action):
    def onConfigure(self):
        self.withLabel("Recognize a digit").withDescription("Recognizes a handwritten digit")
        self.withArg(createImageArgMeta()).withResult(ResultMeta(IntegerType()).withLabel("Recognized digit"))
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
        self.withLabel("Recognize a digit (probabilities)").withDescription("Recognizes a handwritten digit returning probabilities")
        self.withArg(createImageArgMeta()).withResult(ResultMeta(MapType(StringType(), NumberType())).withLabel("Digit probabilities"))
    def onCall(self, image):
        return py4j.facade.predict(image)

def imageClassifierServiceInit(py4jPlugin):
    SpongeUtils.awaitUntil(lambda: py4jPlugin.facade.isReady())
