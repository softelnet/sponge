"""
Sponge Knowledge base
MNIST library
"""

from org.openksavi.sponge.tensorflow.util import ImageUtils

PREDICTION_THRESHOLD = 0.9

class MnistPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.description = "Tries to recognize a handwritten digit"
        self.argsMeta = [createImageArgMeta()]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        predictions = py4j.facade.predict(image)
        prediction = predictions.index(max(predictions))
        return prediction if predictions[prediction] >= PREDICTION_THRESHOLD else None

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