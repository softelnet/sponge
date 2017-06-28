"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicBoolean

# Handles 'alarm' events.
class AlarmTrigger(Trigger):
    def configure(self):
        self.eventName = "alarm"
    def run(self, event):
        print(camel.request("direct:template", event.get("message")))
        EPS.getVariable("alarmSounded").set(True)


# Set initial values for variables.
def onInit():
    EPS.setVariable("alarmSounded", AtomicBoolean(False))

def onStartup():
    EPS.event("alarm").set("message", "Fire in Building A").send()
