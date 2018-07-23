"""
Sponge Knowledge base
MNIST REST server
"""

from org.openksavi.sponge.tensorflow import MnistUtils

PREDICTION_THRESHOLD = 0.75

class MnistPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.argsMeta = [ ArgMeta("image", BinaryType().mimeType("image/png").format("28x28").tags("drawing")).displayName("Image of a digit") ]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        self.logger.info("Action {} called", self.name)
        predictions = py4j.facade.predict(image)
        prediction = predictions.index(max(predictions))
        return prediction if predictions[prediction] >= PREDICTION_THRESHOLD else None

class MnistPredictDetailed(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit (detailed)"
        self.argsMeta = [ ArgMeta("image", BinaryType().mimeType("image/png").format("28x28").tags("drawing")).displayName("Image of a digit") ]
        self.resultMeta = ResultMeta(ListType(NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        self.logger.info("Action {} called", self.name)
        return py4j.facade.predict(image)

class CallPredict(Trigger): 
    def onConfigure(self): 
        self.event = "predict"
    def onRun(self, event):
        file = event.get("file")
        predictions = EPS.call("MnistPredictDetailed", MnistUtils.getImageBytes(file))
        prediction = predictions.index(max(predictions))
        EPS.logger.info("Prediction for {} is: {}, all predictions: {}", file, prediction, predictions)

def onStartup():
    SpongeUtils.awaitUntil(lambda: py4j.facade.isReady())
    EPS.event("predict").set("file", "examples/tensorflow/mnist/data/7_0.png").send()
