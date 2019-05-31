"""
Sponge Knowledge base
Used for testing a gRPC API server and clients.
"""

def onBeforeLoad():
    sponge.addEventType("notification", RecordType().withFields([
        StringType("source").withLabel("Source"),
        IntegerType("severity").withLabel("Severity").withNullable(),
        sponge.getType("Person", "person").withNullable()
    ]))

def onStartup():
    sponge.event("notification").set("source", "Sponge").set("severity", 10).set("person", {"firstName":"James", "surname":"Joyce"}).sendAfter(
        Duration.ZERO, Duration.ofSeconds(1))
