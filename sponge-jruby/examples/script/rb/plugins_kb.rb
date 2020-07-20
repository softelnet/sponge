# Sponge Knowledge Base
# Defining plugins in knowledge base.

def onInit
    # Variables for assertions only
    $sponge.setVariable("valueBefore", nil)
    $sponge.setVariable("valueAfter", nil)
end

# Example plugin defined in the knowledge base.
class ScriptPlugin < Plugin
    def onConfigure(configuration)
        @storedValue = configuration.getString("storedValue", "default")
    end
    def init
        self.logger.debug("Initializing {}", self.name)
    end
    def onStartup
        self.logger.debug("Starting up {}", self.name)
    end
    def getStoredValue
        return @storedValue
    end
    def setStoredValue(value)
        @storedValue = value
    end
end


class PluginTrigger < Trigger
    def onConfigure
        self.withEvent("e1")
    end
    def onRun(event)
        valueBefore = $scriptPlugin.getStoredValue()
        self.logger.info("Plugin stored value: {}", valueBefore)
        $sponge.setVariable("valueBefore", valueBefore)

        $scriptPlugin.setStoredValue(event.get("value"))
        valueAfter = $scriptPlugin.getStoredValue()
        self.logger.info("New stored value: {}", valueAfter)
        $sponge.setVariable("valueAfter", valueAfter)
    end
end

def onStartup
    $sponge.event("e1").set("value", "Value B").send()
end
