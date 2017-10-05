# CPython example

from py4j.java_gateway import JavaGateway

gateway = JavaGateway()

# Remote EPS accessed via Py4J. Note that it doesn't provide easy bean property access for getters and setters.
EPS = gateway.entry_point

print "Connected to {}".format(EPS.getDescription())
EPS.event("helloEvent").set("say", "Hello from Python's Py4J").send()
print "Triggers count: {}, first: {}".format(len(EPS.getEngine().getTriggers()), EPS.getEngine().getTriggers()[0].getName())

EPS.shutdown()
