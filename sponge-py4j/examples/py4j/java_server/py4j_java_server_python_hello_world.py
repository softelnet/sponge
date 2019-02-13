# CPython example
from __future__ import print_function

from py4j.java_gateway import JavaGateway

gateway = JavaGateway()

# The Sponge in other process accessed via Py4J. Note that it doesn't provide a simplified bean property access for getters and setters.
sponge = gateway.entry_point

try:
    print("Connected to {}".format(sponge.getInfo()))
    sponge.event("helloEvent").set("say", "Hello from Python's Py4J").send()
    print("Triggers count: {}, first: {}".format(len(sponge.getEngine().getTriggers()), sponge.getEngine().getTriggers()[0].getMeta().getName()))
finally:
    gateway.shutdown()
