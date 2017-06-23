"""
Sponge Knowledge base
"""

# Sends alarm messages to Camel endpoints in two ways.
class ForwardAlarmTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        # Send the alarm message to all Camel endpoints that use the engine as a consumer.
        camel.send(event.get("message"))

        # Send the alarm message to a specific endpoint.
        camel.send("direct:log", event.get("message"))
