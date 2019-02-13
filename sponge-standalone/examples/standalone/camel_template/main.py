"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicBoolean

# Handles 'alarm' events.
class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        print(camel.requestBody("direct:template", event.get("message")))
        sponge.getVariable("alarmSounded").set(True)


# Set initial values for variables.
def onInit():
    sponge.setVariable("alarmSounded", AtomicBoolean(False))

def onStartup():
    sponge.event("alarm").set("message", "Fire in Building A").send()
