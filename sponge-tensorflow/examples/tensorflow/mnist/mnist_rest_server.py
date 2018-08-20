"""
Sponge Knowledge base
MNIST REST server
"""

def onStartup():
    mnistServiceInit(py4j)
    sponge.event("predict").set("file", "examples/tensorflow/mnist/data/7_0.png").send()
