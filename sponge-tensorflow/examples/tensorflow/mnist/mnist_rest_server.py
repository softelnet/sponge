"""
Sponge Knowledge base
MNIST REST server
"""

class CallPredict(Trigger):
    def onConfigure(self): 
        self.event = "predict"
    def onRun(self, event):
        file = event.get("file")
        predictions = sponge.call("MnistPredictDetailed", [SpongeUtils.readFileToByteArray(file)])
        prediction = predictions.index(max(predictions))
        sponge.logger.info("Prediction for {} is: {}, all predictions: {}", file, prediction, predictions)

def onStartup():
    mnistServiceInit(py4j)
    sponge.event("predict").set("file", "examples/tensorflow/mnist/data/7_0.png").send()
