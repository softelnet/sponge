"""
Sponge Knowledge base
"""

# Sends alarm messages to Camel endpoints in two ways.
class ForwardAlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        # Emits the alarm message to all Camel endpoints that use the engine as a consumer.
        camel.emit(event.get("message"))

        # Send the alarm message to a specific endpoint.
        camel.sendBody("direct:log", event.get("message"))
