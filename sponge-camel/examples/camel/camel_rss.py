"""
Sponge Knowledge base
Camel integration - RSS
"""

from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedCamelMessages", AtomicInteger(0))

class RssTrigger(Trigger):
    def onConfigure(self):
        self.event = "rss"
    def onRun(self, event):
        # self.logger.debug("Received event name={}, body={}", event.name, event.body)
        camel.send("direct:log", event.body)

class RssDecomposedTrigger(Trigger):
    def onConfigure(self):
        self.event = "rssDecomposed"
    def onRun(self, event):
        camel.send("direct:log", event)

class CamelConsumerRssDecomposedTrigger(Trigger):
    def onConfigure(self):
        self.event = "rssDecomposed"
    def onRun(self, event):
        camel.send("News from " + event.get("source") + " - " + event.get("title"))
