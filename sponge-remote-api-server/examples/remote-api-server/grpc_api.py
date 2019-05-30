"""
Sponge Knowledge base
Used for testing a gRPC API server and clients.
"""

def onBeforeLoad():
    sponge.addEventType("notification", RecordType().withFields([
        StringType("source").withLabel("Source"),
        IntegerType("severity").withLabel("Severity").withNullable()
    ]))

def onStartup():
    sponge.event("notification").set("source", "Sponge").sendAfter(Duration.ZERO, Duration.ofSeconds(1))
