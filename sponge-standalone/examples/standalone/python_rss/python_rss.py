"""
Sponge Knowledge base
Standalone with Camel example
"""

from org.openksavi.sponge.camel import ScriptRouteBuilder
from org.openksavi.sponge.camel import CamelUtils
from org.apache.camel.support.processor.idempotent import MemoryIdempotentRepository
from org.apache.camel.language.xpath import XPathBuilder
from java.util.concurrent.atomic import AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedRssCount", AtomicInteger(0))

class RssTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("rss")
    def onRun(self, event):
        camel.emit("[" + event.get("channel") + "] " + event.get("title"))

class PythonRoute(ScriptRouteBuilder):
    def configure(self):
        self.fromS("rss:http://rss.cnn.com/rss/edition.rss?sortEntries=false&consumer.delay=1000").to("direct:rss")

        self.fromS("rss:http://feeds.bbci.co.uk/news/world/rss.xml?consumer.delay=1000").to("direct:rss")

        self.fromS("direct:rss").routeId("rss") \
            .marshal().rss() \
            .idempotentConsumer(self.xpath("/rss/channel/item/title/text()"), MemoryIdempotentRepository.memoryIdempotentRepository()) \
            .process(camel.processor(lambda exchange: exchange.getIn().setBody(sponge.event("rss") \
                .set("channel", CamelUtils.xpath(exchange, "/rss/channel/title/text()")) \
                .set("title", CamelUtils.xpath(exchange, "/rss/channel/item/title/text()")) \
                .set("link", CamelUtils.xpath(exchange, "/rss/channel/item/link/text()")) \
                .set("description", CamelUtils.xpath(exchange, "/rss/channel/item/description/text()")) \
                    .make()))) \
            .to("sponge:spongeEngine")

        self.fromS("sponge:spongeEngine").routeId("spongeConsumerCamelPython") \
            .transform().simple("${body}") \
            .process(camel.processor(lambda exchange: sponge.getVariable("receivedRssCount").incrementAndGet())) \
            .to("stream:out")

def onStartup():
    camel.context.addRoutes(PythonRoute())
