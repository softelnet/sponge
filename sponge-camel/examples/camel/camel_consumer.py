"""
Sponge Knowledge base
Camel integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("receivedCamelMessage", AtomicBoolean(False))

class CamelTrigger(Trigger):
    def configure(self):
        self.event = "spongeEvent"
    def run(self, event):
        camel.send(event.get("message"))

def onStartup():
    # Send an event with a delay to allow starting of Camel before sending the message.
    EPS.event("spongeEvent").set("message", "Send me to Camel").sendAfter(1000)
