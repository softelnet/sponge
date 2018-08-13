"""
Sponge Knowledge base
MNIST REST server
"""

from org.openksavi.sponge.tensorflow.util import ImageUtils

PREDICTION_THRESHOLD = 0.75

IMAGE_ARG_META = ArgMeta("image", BinaryType().mimeType("image/png")
        .features({"source":"drawing", "width":28, "height":28, "background":"black", "color":"white", "strokeWidth":2}))\
            .displayName("Image of a digit")

class MnistPredict(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit"
        self.description = "Tries to recognize a handwritten digit"
        self.argsMeta = [IMAGE_ARG_META]
        self.resultMeta = ResultMeta(IntegerType()).displayName("Recognized digit")
    def onCall(self, image):
        predictions = py4j.facade.predict(image)
        prediction = predictions.index(max(predictions))
        return prediction if predictions[prediction] >= PREDICTION_THRESHOLD else None

class MnistPredictDetailed(Action):
    def onConfigure(self):
        self.displayName = "Recognize a digit (probabilities)"
        self.description = "Tries to recognize a handwritten digit returning a probabilities list"
        self.argsMeta = [IMAGE_ARG_META]
        self.resultMeta = ResultMeta(ListType(NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        return py4j.facade.predict(image)

class MnistLearn(Action):
    def onConfigure(self):
        self.displayName = "Learn a digit"
        self.description = "Manually learns the model to recognize a digit"
        self.argsMeta = [IMAGE_ARG_META, ArgMeta("digit", IntegerType()).displayName("Digit")]
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
