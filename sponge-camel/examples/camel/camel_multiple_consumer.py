"""
Sponge Knowledge base
Camel integration
"""

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spongeProducer")
    def onRun(self, event):
        self.logger.debug("Received event name={}, body={}", event.name, event.body)
        message = "Hello World!"
        camel.sendBody("direct:log", message)
        camel.sendBody("direct:end", message)

        for consumer in camel.consumers:
            newExchange = consumer.endpoint.createExchange("CUSTOM CONSUMER")
            consumer.emit(newExchange)

class CamelConsumerTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("e2")
    def onRun(self, event):
        self.logger.debug("Message for e2 event: {}", event.get("message"))
        camel.emit(event.get("message"))

def onStartup():
    sponge.event("e2").set("message", "Hi there").send()

