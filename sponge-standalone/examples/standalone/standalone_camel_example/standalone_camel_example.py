"""
Sponge Knowledge base
Standalone with Camel example
"""

from org.openksavi.sponge.camel import ScriptRouteBuilder

def onInit():
    # Variables for assertions only
    EPS.setVariable("sent", False)
    EPS.setVariable("message", None)

class CamelTrigger(Trigger):
    def configure(self):
        self.event = "spongeEvent"
    def run(self, event):
        message = spring.context.getBean("spongeEngine").description + ": " + event.get("message")
        camel.send(message)
        EPS.setVariable("sent", True)

class PythonRoute(ScriptRouteBuilder):
    def configure(self):
        self.fromS("sponge:spongeEngine").routeId("spongeConsumerCamelPython") \
                .transform().simple("Python route - Received message: ${body}") \
                .process(lambda exchange: EPS.setVariable("message", exchange.getIn().getBody())) \
                .to("stream:out")

def onStartup():
    camel.context.addRoutes(PythonRoute())
    EPS.event("spongeEvent").set("message", "Send me to Camel").send()
