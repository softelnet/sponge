"""
Sponge Knowledge base
Digits REST server
"""

class CallPredict(Trigger):
    def onConfigure(self): 
        self.withEvent("predict")
    def onRun(self, event):
        file = event.get("file")
        predictions = sponge.call("DigitsPredictProbabilities", [SpongeUtils.readFileToByteArray(file)])
        sponge.logger.info("Predictions for {}: {}", file, predictions)

def onStartup():
    imageClassifierServiceInit(py4j)
    sponge.event("predict").set("file", "examples/tensorflow/digits/data/7_0.png").send()
