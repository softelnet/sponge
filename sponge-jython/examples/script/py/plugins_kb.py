"""
Sponge Knowledge Base
Defining plugins in knowledge base.
"""

def onInit():
    # Variables for assertions only
    sponge.setVariable("valueBefore", None)
    sponge.setVariable("valueAfter", None)

# Example plugin defined in the knowledge base.
class ScriptPlugin(Plugin):
    def onConfigure(self, configuration):
        self.storedValue = configuration.getString("storedValue", "default")
    def onInit(self):
        self.logger.debug("Initializing {}", self.name)
    def onStartup(self):
        self.logger.debug("Starting up {}", self.name)
    def getStoredValue(self):
        return self.storedValue
    def setStoredValue(self, value):
        self.storedValue = value


class PluginTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("e1")
    def onRun(self, event):
    	valueBefore = scriptPlugin.getStoredValue()
    	self.logger.info("Plugin stored value: {}", valueBefore)
        sponge.setVariable("valueBefore", valueBefore)
    	scriptPlugin.setStoredValue(event.get("value"))
        valueAfter = scriptPlugin.getStoredValue()
        self.logger.info("New stored value: {}", valueAfter)
        sponge.setVariable("valueAfter", valueAfter)

def onStartup():
    sponge.event("e1").set("value", "Value B").send()
