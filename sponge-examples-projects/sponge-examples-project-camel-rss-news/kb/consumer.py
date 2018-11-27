"""
Sponge Knowledge base
"""

# Sends alarm messages to Camel endpoints in two ways.
class ForwardAlarmTrigger(Trigger):
    def onConfigure(self):
        self.event = "alarm"
    def onRun(self, event):
        # Emit the alarm message to all Camel endpoints that use the engine as a consumer.
        camel.emit(event.get("message"))

        # Send the alarm message to a specific endpoint.
        camel.sendBody("direct:log", event.get("message"))

        sponge.getVariable("alarmForwarded").set(True)
