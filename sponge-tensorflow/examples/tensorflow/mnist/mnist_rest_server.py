"""
Sponge Knowledge base
MNIST REST server
"""

from org.openksavi.sponge.tensorflow.util import ImageUtils

PREDICTION_THRESHOLD = 0.55

IMAGE_ARG_META = ArgMeta("image", BinaryType().mimeType("image/png").tag("drawing")
                    .features({"width":"28", "height":"28", "background":"black", "color":"white", "strokeWidth":"2"})).displayName("Image of a digit")

class MnistPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.argsMeta = [IMAGE_ARG_META]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        self.logger.info("Action {} called", self.name)
        #ImageUtils.writeImageBytes(image, "test.png")
        predictions = py4j.facade.predict(image)
        prediction = predictions.index(max(predictions))
        return prediction if predictions[prediction] >= PREDICTION_THRESHOLD else None

class MnistPredictDetailed(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit (detailed)"
        self.argsMeta = [IMAGE_ARG_META]
        self.resultMeta = ResultMeta(ListType(NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        self.logger.info("Action {} called", self.name)
        return py4j.facade.predict(image)

class CallPredict(Trigger): 
    def onConfigure(self): 
        self.event = "predict"
    def onRun(self, event):
        file = event.get("file")
        predictions = sponge.call("MnistPredictDetailed", ImageUtils.getImageBytes(file))
        prediction = predictions.index(max(predictions))
        sponge.logger.info("Prediction for {} is: {}, all predictions: {}", file, prediction, predictions)

def onStartup():
    SpongeUtils.awaitUntil(lambda: py4j.facade.isReady())
    sponge.event("predict").set("file", "examples/tensorflow/mnist/data/7_0.png").send()
