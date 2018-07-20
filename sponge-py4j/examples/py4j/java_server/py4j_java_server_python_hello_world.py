# CPython example
from __future__ import print_function

from py4j.java_gateway import JavaGateway

gateway = JavaGateway()

#  EPS in other process accessed via Py4J. Note that it doesn't provide a simplified bean property access for getters and setters.
EPS = gateway.entry_point

print("Connected to {}".format(EPS.getInfo()))
EPS.event("helloEvent").set("say", "Hello from Python's Py4J").send()
print("Triggers count: {}, first: {}".format(len(EPS.getEngine().getTriggers()), EPS.getEngine().getTriggers()[0].getName()))

gateway.shutdown()
