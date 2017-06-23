"""
Sponge Knowledge base
Defining plugins in knowledge base.
"""

def onInit():
    # Variables for assertions only
    EPS.setVariable("valueBefore", None)
    EPS.setVariable("valueAfter", None)

# Example plugin defined in the knowledge base.
class ScriptPlugin(Plugin):
    def configure(self, configuration):
        self.storedValue = configuration.getString("storedValue", "default")
    def init(self):
        self.logger.debug("Initializing {}", self.name)
    def onStartup(self):
        self.logger.debug("Starting up {}", self.name)
    def getStoredValue(self):
        return self.storedValue
    def setStoredValue(self, value):
        self.storedValue = value


class PluginTrigger(Trigger):
    def configure(self):
        self.eventName = "e1"
    def run(self, event):
    	valueBefore = scriptPlugin.getStoredValue()
    	self.logger.info("Plugin stored value: {}", valueBefore)
        EPS.setVariable("valueBefore", valueBefore)
    	scriptPlugin.setStoredValue(event.get("value"))
        valueAfter = scriptPlugin.getStoredValue()
        self.logger.info("New stored value: {}", valueAfter)
        EPS.setVariable("valueAfter", valueAfter)

def onStartup():
    EPS.event("e1").set("value", "Value B").send()
