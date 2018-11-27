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
        camel.sendBody("direct:log", event.body)

class RssDecomposedTrigger(Trigger):
    def onConfigure(self):
        self.event = "rssDecomposed"
    def onRun(self, event):
        camel.sendBody("direct:log", event)

class CamelConsumerRssDecomposedTrigger(Trigger):
    def onConfigure(self):
        self.event = "rssDecomposed"
    def onRun(self, event):
        camel.emit("News from " + event.get("source") + " - " + event.get("title"))
