"""
Sponge Knowledge base
Camel integration - RSS
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    EPS.setVariable("receivedCamelMessages", AtomicInteger(0))

class RssTrigger(Trigger):
    def configure(self):
        self.eventName = "rss"
    def run(self, event):
        # self.logger.debug("Received event name={}, body={}", event.name, event.body)
        camel.send("direct:log", event.body)

class RssDecomposedTrigger(Trigger):
    def configure(self):
        self.eventName = "rssDecomposed"
    def run(self, event):
        camel.send("direct:log", event)

class CamelConsumerRssDecomposedTrigger(Trigger):
    def configure(self):
        self.eventName = "rssDecomposed"
    def run(self, event):
        camel.send("News from " + event.get("source") + " - " + event.get("title"))
