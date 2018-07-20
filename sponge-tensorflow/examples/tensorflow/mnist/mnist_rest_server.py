"""
Sponge Knowledge base
MNIST REST server
"""

from org.openksavi.sponge.tensorflow import MnistUtils

class MnistPredict(Action):
    def onConfigure(self):
        self.argsMeta = [ ArgMeta("image", BinaryType().format("png")).displayName("PNG 28x28 image of a digit") ]
        self.resultMeta = ResultMeta(ListType(NumberType())).displayName("Digit probabilities")
    def onCall(self, image):
        self.logger.info("Action {} called", self.name)
        return py4j.facade.predict(image)

class CallPredict(Trigger): 
    def onConfigure(self): 
        self.event = "predict"
    def onRun(self, event):
        file = event.get("file")
        predictions = EPS.call("MnistPredict", MnistUtils.getImageBytes(file))
        prediction = predictions.index(max(predictions))
        EPS.logger.info("Prediction for {} is: {}, all predictions: {}", file, prediction, predictions)

def onStartup():
    EPS.event("predict").set("file", "examples/tensorflow/mnist/data/7_0.png").sendAfter(Duration.ofSeconds(5))
