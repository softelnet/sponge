"""
Sponge Knowledge base
Camel integration
"""

class CamelTrigger(Trigger):
    def configure(self):
        self.event = "spongeProducer"
    def run(self, event):
        self.logger.debug("Received event name={}, body={}", event.name, event.body)
        message = "Hello World!"
        camel.send("direct:log", message)
        camel.send("direct:end", message)

        for consumer in camel.consumers:
            newExchange = consumer.endpoint.createExchange("CUSTOM CONSUMER")
            consumer.send(newExchange)

class CamelConsumerTrigger(Trigger):
    def configure(self):
        self.event = "e2"
    def run(self, event):
        self.logger.debug("Message for e2 event: {}", event.get("message"))
        camel.send(event.get("message"))

def onStartup():
    EPS.event("e2").set("message", "Hi there").send()

