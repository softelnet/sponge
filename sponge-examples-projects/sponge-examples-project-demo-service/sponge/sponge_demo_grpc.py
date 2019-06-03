"""
Sponge Knowledge base
gRPC demo.
"""

def onBeforeLoad():
    sponge.addType("Person", lambda: RecordType().withFields([
        StringType("firstName").withLabel("First name"),
        StringType("surname").withLabel("Surname")
    ]))

    sponge.addEventType("notification", RecordType().withFields([
        StringType("source").withLabel("Source"),
        IntegerType("severity").withLabel("Severity").withNullable(),
        sponge.getType("Person", "person").withNullable()
    ]))

def onStartup():
    # Enable support processors in this knowledge base.
    grpcApiServer.enableSupport(sponge)

    sponge.event("notification").set({"source":"Sponge", "severity":10,
            "person":{"firstName":"James", "surname":"Joyce"}}).label("The notification").description("The new event notification").sendEvery(Duration.ofSeconds(10))
