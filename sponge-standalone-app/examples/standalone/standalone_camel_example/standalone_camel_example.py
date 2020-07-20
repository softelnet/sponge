"""
Sponge Knowledge Base
Standalone with Camel example
"""

from org.openksavi.sponge.camel import ScriptRouteBuilder

def onInit():
    # Variables for assertions only
    sponge.setVariable("sent", False)
    sponge.setVariable("message", None)

class CamelTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spongeEvent")
    def onRun(self, event):
        message = spring.context.getBean("spongeEngine").info + ": " + event.get("message")
        camel.emit(message)
        sponge.setVariable("sent", True)

class PythonRoute(ScriptRouteBuilder):
    def configure(self):
        self.fromS("sponge:spongeEngine").routeId("spongeConsumerCamelPython") \
                .transform().simple("Python route - Received message: ${body}") \
                .process(lambda exchange: sponge.setVariable("message", exchange.getIn().getBody())) \
                .to("stream:out")

def onStartup():
    camel.context.addRoutes(PythonRoute())
    sponge.event("spongeEvent").set("message", "Send me to Camel").send()
