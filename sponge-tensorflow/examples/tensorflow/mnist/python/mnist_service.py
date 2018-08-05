from __future__ import print_function

from py4j.clientserver import ClientServer

import mnist_model as model

class MnistService(object):
    def __init__(self):
        self.ready = False
    def startup(self):
        self.model = model.MnistModel()
        self.model.load()
        self.ready = True
    def isReady(self):
        return self.ready
    def predict(self, image_data):
        predictions = self.model.predict(image_data).tolist()
        result = gateway.jvm.java.util.ArrayList()
        for prediction in predictions:
            result.add(prediction)
        return result
    class Java:
        implements = ["org.openksavi.sponge.tensorflow.MnistService"]

mnistService = MnistService()
gateway = ClientServer(python_server_entry_point=mnistService)
mnistService.startup()

print("MNIST service has started.", flush=True)
